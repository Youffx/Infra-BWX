/**
 * Google Apps Script backend for Infra BWX
 * Deploy as Web App with "Anyone" access.
 *
 * Setup:
 * 1. Create folder "InfraBWX_Images" in Google Drive root
 * 2. A Google Sheet "InfraBWX_Data" will be auto-created with columns:
 *    Timestamp, ImageId, Category, Latitude, Longitude, Kecamatan, ImageUrl
 * 3. Deploy as Web App
 * 4. Copy deployment URL into RetrofitClient.kt
 */

var CONFIG = {
  FOLDER_NAME: "InfraBWX_Images",
  SHEET_NAME: "InfraBWX_Data",
  SHEET_HEADERS: ["Timestamp", "ImageId", "Category", "Latitude", "Longitude", "Kecamatan", "ImageUrl"]
};

function doGet(e) {
  var action = e.parameter.action;
  var category = e.parameter.category;

  if (action === "getRanking" && category) {
    return getRanking(category);
  }

  if (action === "getLocations") {
    return getLocations();
  }

  return jsonResponse({ status: "error", message: "Invalid parameters" });
}

function doPost(e) {
  try {
    var data = JSON.parse(e.postData.contents);
    var imageBase64 = data.image;
    var category = data.category;
    var latitude = data.latitude;
    var longitude = data.longitude;
    var kecamatan = data.kecamatan;

    if (!imageBase64 || !category || !latitude || !longitude || !kecamatan) {
      return jsonResponse({ status: "error", message: "Semua field harus diisi" });
    }

    var imageId = saveImageToDrive(imageBase64, category, kecamatan);
    var imageUrl = "https://drive.google.com/uc?export=view&id=" + imageId;
    appendToSheet(category, latitude, longitude, kecamatan, imageId, imageUrl);

    return jsonResponse({ status: "success", message: "Laporan berhasil dikirim" });
  } catch (err) {
    return jsonResponse({ status: "error", message: err.toString() });
  }
}

function saveImageToDrive(base64Data, category, kecamatan) {
  var folder = getOrCreateFolder(CONFIG.FOLDER_NAME);
  var timestamp = new Date().getTime();
  var fileName = category + "_" + kecamatan + "_" + timestamp + ".jpg";
  var decodedData = Utilities.base64Decode(base64Data);
  var blob = Utilities.newBlob(decodedData, "image/jpeg", fileName);
  return folder.createFile(blob).getId();
}

function appendToSheet(category, latitude, longitude, kecamatan, imageId, imageUrl) {
  var sheet = getOrCreateSheet(CONFIG.SHEET_NAME);
  var timestamp = Utilities.formatDate(new Date(), "Asia/Jakarta", "yyyy-MM-dd HH:mm:ss");
  sheet.appendRow([timestamp, imageId, category, latitude, longitude, kecamatan, imageUrl]);
}

function getRanking(category) {
  var sheet = getOrCreateSheet(CONFIG.SHEET_NAME);
  var data = sheet.getDataRange().getValues();

  if (data.length <= 1) {
    return jsonResponse({ status: "success", data: [] });
  }

  var rows = data.slice(1);
  var filtered = rows.filter(function(row) { return row[2] === category; });
  var ranking = {};

  filtered.forEach(function(row) {
    var kec = row[5];
    ranking[kec] = (ranking[kec] || 0) + 1;
  });

  var result = Object.keys(ranking).map(function(kec) {
    return { kecamatan: kec, jumlah: ranking[kec] };
  });

  result.sort(function(a, b) { return b.jumlah - a.jumlah; });

  return jsonResponse({ status: "success", data: result });
}

function getLocations() {
  var sheet = getOrCreateSheet(CONFIG.SHEET_NAME);
  var data = sheet.getDataRange().getValues();

  if (data.length <= 1) {
    return jsonResponse({ status: "success", data: [] });
  }

  var rows = data.slice(1);
  var locations = rows.map(function(row) {
    return {
      latitude: parseFloat(row[3]),
      longitude: parseFloat(row[4]),
      kecamatan: row[5],
      category: row[2]
    };
  }).filter(function(loc) {
    return !isNaN(loc.latitude) && !isNaN(loc.longitude);
  });

  return jsonResponse({ status: "success", data: locations });
}

function getOrCreateFolder(folderName) {
  var folders = DriveApp.getFoldersByName(folderName);
  return folders.hasNext() ? folders.next() : DriveApp.createFolder(folderName);
}

function getOrCreateSheet(sheetName) {
  var files = DriveApp.getFilesByName(sheetName);
  var file = files.hasNext() ? files.next() : SpreadsheetApp.create(sheetName);
  var sheet = SpreadsheetApp.openById(file.getId()).getActiveSheet();
  if (sheet.getLastRow() === 0) {
    sheet.appendRow(CONFIG.SHEET_HEADERS);
  }
  return sheet;
}

function jsonResponse(payload) {
  return ContentService
    .createTextOutput(JSON.stringify(payload))
    .setMimeType(ContentService.MimeType.JSON);
}
