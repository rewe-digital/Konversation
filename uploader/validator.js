///<reference path="node_modules/@types/node/index.d.ts"/>

const commander = require('commander');

// >ask simulate -p rewe -l de-DE -s amzn1.ask.skill.13bc0f62-98ad-452a-b804-e3820f9b72ec -t "frag rewe demo nach angeboten"

require('./node_modules/ask-cli/lib/simulate/simulate').createCommand(commander);

//commander
//    .description('Command Line Interface for Alexa Skill Kit')
//    .parse([process.argv[0],process.argv[1], "simulate", "-p", "rewe", "-l", "de-DE", "-s", "amzn1.ask.skill.13bc0f62-98ad-452a-b804-e3820f9b72ec", "-f", "../test.txt"]);
//
//throw Error();

//commander
//    .description('Command Line Interface for Alexa Skill Kit')
//    .parse([process.argv[0],process.argv[1], "simulate", "-p", "rewe", "-l", "de-DE", "-s", "amzn1.ask.skill.13bc0f62-98ad-452a-b804-e3820f9b72ec", "-t", "frag rewe demo nach angeboten"]);

var skillId = "amzn1.ask.skill.13bc0f62-98ad-452a-b804-e3820f9b72ec";

const path = require('path');
const fs = require('fs');
const apiWrapper = require('./node_modules/ask-cli/lib/api/api-wrapper.js');
const jsonUtility = require('./node_modules/ask-cli/lib/utils/json-utility');
const profileHelper = require('./node_modules/ask-cli/lib/utils/profile-helper');
const tools = require('./node_modules/ask-cli/lib/utils/tools');
const CONSTANTS = require('./node_modules/ask-cli/lib/utils/constants');
const Spinner = require('cli-spinner').Spinner;
const POLLING_INTERVAL = 333;

const sem = require('semaphore')(1);
const intent = process.argv[2];
let pass = 0;
let fail = [];
if(intent) {
    const intentName = intent.substring((intent.lastIndexOf('\\') || intent.lastIndexOf('/')) + 1, intent.lastIndexOf('.'));
    fs.readFileSync(intent, 'utf-8').split("\n").forEach((line, i, all) => {
        if(line.length) {
            sem.take(() => {
                callSimulator(line.trim(), intentName, sem.leave.bind(sem))
            });
        }
        if(i === all.length) {
            console.log(`${pass} passed, ${fail.length} failed.\nList of Failed:\n${fail}`)
        }
    });
} else {
    console.warn("no file given")
}

function callSimulator(text, intentName, callback) {
    console.log("Asking Alexa: " + text);

    const options = {
        profile: "rewe",
        locale: "de-DE",
        text: "frag rewe demo nach " + text,
        file: null,
        debug: false
    };

    let profile = profileHelper.runtimeProfile(options.profile);
    let locale = options.locale || process.env.ASK_DEFAULT_DEVICE_LOCALE;
    let dataCallback = function(data) {
        let response = tools.convertDataToJsonObject(data);
        if(response) {
            const info = response.result.skillExecutionInfo;
            //console.log("Used endpoint: " + info.invocationRequest.endpoint);
            //console.log("Detected Intent: " + info.invocationRequest.body.request.intent.name);
            const matchedIntent = info.invocationRequest.body.request.intent.name;
            if(intentName !== matchedIntent) {
                fail[fail.length]=text;
                console.warn(`${text} was detected as intent "${matchedIntent}" instead of "${intentName}".`);
                try {
                    console.log(JSON.stringify(info.invocationResponse.body.response.outputSpeech, null, 2));
                } catch(e) {
                    console.log(JSON.stringify(info, null, 2));
                }
            } else pass++;
        } else {
            console.warn("error")
        }
        if(callback) callback();
    };

    let listenSpinner = new Spinner({text: 'Waiting for simulation response', stream: process.stderr});
    listenSpinner.setSpinnerString(process.platform !== 'darwin' ? '|/-\\' : '◜◠◝◞◡◟');

    apiWrapper.callSimulateSkill(options.file, options.text, skillId, locale, profile, options.debug, (data) => {
        let response = tools.convertDataToJsonObject(data.body);
        if(response) {
            let simulationId = response.id;
            //console.error('✓ Simulation created for simulation id: ' + simulationId);
            //listenSpinner.start();

            let pollSimulationResult = (responseBody) => {
                let response = tools.convertDataToJsonObject(responseBody);
                if(response) {
                    if(!response.hasOwnProperty('status')) {
                        listenSpinner.stop();
                        console.error('𐄂 Unable to get skill simulation result for simulation id: ' + simulationId);
                    } else if(response.status === CONSTANTS.SKILL.SIMULATION_STATUS.IN_PROGRESS) {
                        setTimeout(() => {
                            apiWrapper.callGetSimulation(simulationId, skillId, profile, options.debug, (data) => {
                                pollSimulationResult(data.body);
                            });
                        }, POLLING_INTERVAL);
                    } else if(response.status === CONSTANTS.SKILL.SIMULATION_STATUS.SUCCESS ||
                        response.status === CONSTANTS.SKILL.SIMULATION_STATUS.FAILURE) {
                        listenSpinner.stop();
                        dataCallback(responseBody);
                    } else {
                        listenSpinner.stop();
                        console.error('𐄂 [Error]: Invalid response for skill simulation');
                    }
                }
            };
            pollSimulationResult(data.body);
        }
    });
}