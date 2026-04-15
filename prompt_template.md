# Automate API based on below instructions

## Title
[Add your prompt title here]

## Description

You are a Backend QA Automation Engineer. You need to follow the instructions and automate the API.
This Framework has already folder structure and all setup done. You just need to follow instructions and make the changes.

Below API is responsible to fetch broker Active hours time.
This is API resource - /api/v1/misc/amoActiveHours
method - POST
payload - {"brokerName":"{{broker}}","options":{},"context":{"brokerFunctionLogId":"e63ef361-0a27-4f06-a5d6-94967dbe472c"}}
broker can be - groww, kite, hdfc, iifl
staging_broker url - https://scb.stag.smallcase.com
production_broker url - https://scb.prod.smallcase.com

--header 'x-domain-token: ZcdZ0QkzdF' \
--header 'Content-Type: application/json'

Response structure - {
    "error": false,
    "response": {
        "place": {
            "start": 1760607360000,
            "end": 1760672640000
        },
        "cancel": {
            "start": 1760607360000,
            "end": 1760672640000
        }
    }
}

## Instructions
Step 1 - Create variable with name broker_url in properties file based on staging and production information.
Step 2 - Create one package in test with the name of integration and then create one java class with name AmoActiveHoursAPI.
Step 3 - Add API resource in SmallcaseResource class the way it is written. Don't add your thoughts on that.
Step 4 - Add test script to automate the API which is mentioned in description in the java class.
Step 5 - In the test script you need to take payload in map format and you should write in the same format how it is written for other test scripts.
Step 6 - You need to add logger and report in test script.
Step 7 - Inside payload broker is parameterized which we need to take from TestNG xml file and same need to use in java class with TestNG parameter.

Step 8 - You will get response in above format which is in unix time format. You need to convert it to IST time first then in report please
add converted time and actual response so that in report we should be able to get those properly.
step 9 - There is a json file in this path \src\main\java\resource\testData\broker_config.json where we have broker specific AMO window timing for Working days  after completing step 8 u need to add these validation check in test class also some how u need to add this validation for working days from monday to friday . for Saturday sunday AMO window open entire days so need to manage both condition.
stpe 10 - If any case fail in step 9 condition u need to add in report also 






## Example
[Add example usage if needed]

## Notes
Once you have done writing everything please do share all of your changes file. Thanks. 
---
*Created: [Date]*
*Author: [Your Name]*
