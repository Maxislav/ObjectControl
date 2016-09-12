/**
 * Created by maxislav on 10.08.16.
 */


const fs = require('fs');
const exec = require('child_process').exec;

fs.readFile('pass-config.json', (err, data) => {
  if (err) throw err;

  let pass = JSON.parse(data.toString()).pass

  console.log(pass);



  exec("cd app/; zip -P \""+pass+"\" build.gradle.zip -r build.gradle; git  ", (error, stdout, stderr) => {
    if (error) {
      console.error(`exec error: ${error}`);
      return;
    }
    console.log(`stdout: ${stdout}`);
    console.log(`stderr: ${stderr}`);
  });
  
  
});


