const fs = require('fs');
const path = require('path');
require('dotenv').config();

const backendUrl = process.env.BACKEND_URL;

// Replace placeholder with the enviromental variable
const filePath = path.join(__dirname, 'webapp', 'login.html');
const htmlContent = fs.readFileSync(filePath, 'utf-8');
const updatedContent = htmlContent.replace(/BACKEND_URL: ".*?"/, `BACKEND_URL: "${backendUrl}"`);
fs.writeFileSync(filePath, updatedContent);

console.log('Replaced BACKEND_URL with:', backendUrl);