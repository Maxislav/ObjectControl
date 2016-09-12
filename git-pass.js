/**
 * Created by maxislav on 10.08.16.
 */

const fs = require('fs');
const exec = require('child_process').exec;

fs.readFile('pass-config.json', (err, data) => {
  if (err) throw err;

  let pass = JSON.parse(data.toString()).pass


  onExec("cd app/; zip -P \""+pass+"\" build.gradle.zip -r build.gradle")
  .then((d)=>{
     return onExec("git add * ; git commit -m \"archive pass\"; git push ")
  })
});

function onExec(str){
    return new Promise((resolve, reject)=>{
         exec(str, (error, stdout, stderr) => {
                if (error) {
                  console.error(`exec error: ${error}`);
                  reject(error)
                  return;
                }
                console.log(`stdout: ${stdout}`);
                console.log(`stderr: ${stderr}`);
                resolve(stdout)
              });
    })

}


