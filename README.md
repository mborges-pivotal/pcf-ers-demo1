[![Build Status](https://travis-ci.org/mborges-pivotal/pcf-ers-demo1.svg?branch=master)](https://travis-ci.org/mborges-pivotal/pcf-ers-demo1)
[ ![Download](https://api.bintray.com/packages/mborges-pivotal/generic/pcf-ers-demo1/images/download.svg) ](https://bintray.com/mborges-pivotal/generic/pcf-ers-demo1/_latestVersion)

# PCF Elastic Runtime Service (ERS) Base Demo
Base application to demonstrate PCF ERS

## Credits and contributions
As you all know, we often transform other work into our own. This is all based from Andrew Ripka's [cf-workshop-spring-boot github repo](https://github.com/pivotal-cf-workshop/cf-workshop-spring-boot) with some basic modifications.

## Introduction
This base application is intended to demonstrate some of the basic functionality of PCF ERS:

* PCF api, target, login, and push
* PCF environment variables
  * Spring Cloud Profiles
* Scaling, self-healing, router and load balancing
* RDBMS service and application auto-configuration
* Blue green deployments

## Getting Started

**Prerequisites**
- [Cloud Foundry CLI](http://info.pivotal.io/p0R00I0eYJ011dAUCN06lR2)
- [Git Client](http://info.pivotal.io/i1RI0AUe6gN00C010l12J0R)
- An IDE, like [Spring Tool Suite](http://info.pivotal.io/f00RC0N0lh01eU21IAJ260R)
- [Java SE Development Kit](http://info.pivotal.io/n0I60i3021AN0JU0le10CRR)

**Building**
```
$ git clone [REPO]
$ cd [REPO]
$ ./mvnw clean install
``` 

### To run the application locally
The application is set to use an embedded H2 database in non-PaaS environments, and to take advantage of Pivotal CF's auto-configuration for services. To use a MySQL Dev service in PCF, simply create and bind a service to the app and restart the app. No additional configuration is necessary when running locally or in Pivotal CF.

In Pivotal CF, it is assumed that a Pivotal MySQL service will be used.

```
$ ./mvnw spring-boot:run
```

Then go to the http://localhost:8080 in your browser

### Running on Cloud Foundry
Take a look at the manifest file for the recommended setting. Adjust them as per your environment.

## Demo Scripts summary
The application tries to be self-descriptive. You'll see when you access the application.

## Jenkins Integration

* Fork this repository to your Git account

* At Jenkins, create a new Job

* Choose "this build is parameterized" option and include the following parameters:

```
[String parameter] CF_SYSTEM_DOMAIN <PCF api, ex: https://api.local.pcfdev.io>
[String parameter] CF_APPS_DOMAIN <apps domain, ex: local.pcfdev.io>
[String parameter] CF_USER 
[Password parameter] CF_PASSWORD
[String parameter] CF_ORG
[String parameter] CF_SPACE
[String parameter] CF_APP <apps name: attendees>
[String parameter] CF_JAR <path to jar: target/pcf-ers-demo1-0.0.1-SNAPSHOT.jar>
```

* At "Source code management" add the git repository

```
https://github.com/<your user>/pcf-ers-demo1
```

* At "Build" add the option "Execute shell script" and paste the following script:

```
./mvnw clean install

# login to the cf api
cf login -a $CF_SYSTEM_DOMAIN -u $CF_USER -p $CF_PASSWORD -o $CF_ORG -s $CF_SPACE --skip-ssl-validation

# push the app
cf push "$CF_APP" 

APP_NAME="$CF_APP"
URL="$(/usr/local/bin/cf app $APP_NAME | grep URLs| cut -c7-)"
```

* Save the configuration

* Go to your project initial page and click on "Run with parameters" at the left side

* To see the logs, click on the build number and then click on console output

* Your app should be online
