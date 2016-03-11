[![Build Status](https://travis-ci.org/mborges-pivotal/pcf-ers-demo1.svg?branch=master)](https://travis-ci.org/mborges-pivotal/pcf-ers-demo1)
[ ![Download](https://api.bintray.com/packages/mborges-pivotal/generic/pcf-ers-demo1/images/download.svg) ](https://bintray.com/mborges-pivotal/generic/pcf-ers-demo1/_latestVersion)

# PCF Elastic Runtime Service (ERS) Base Demo
Base application to demonstrate PCF ERS

## Credits and contributions
As you all know, we often transform other work into our own. This is all based from Andrew Ripka's [cf-workshop-spring-boot github repo](https://github.com/pivotal-cf-workshop/cf-workshop-spring-boot) with some basic modifications as follows:  

- Split into client (REST client and UI) and server (service)
- Introduce thymeleaf fragments
- Introduce Spring Cloud Netflix client projects (Hystrix and Feign with Ribbon, but no Eureka)
- Introduce Spring profiles
- Basic Concource CI / CD (create tracker?)

Note: Our goal is to have the blueprint to deploying a cloud native application based on Spring Cloud and Spring Framework.

## Introduction
This base application is intended to demonstrate some of the basic functionality of PCF ERS:

* PCF api, target, login, and push
* PCF environment variables
  * Spring Cloud Profiles
* Scaling, self-healing, router and load balancing
* RDBMS service and application auto-configuration
* Spring Boot, Data (JPA, REST, HATEOS), Cloud, Netflix 

Spring Cloud Netflix (Client focus)

* Feign - declarative REST Client w/ Ribbon (no Eureka)
* Hystric circuit breaker and dashboard

## Getting started
Start by downloading the course materials.  This can be accomplished either through the GitHub website or if you have Git installed, use the following commands:

```
$ git clone https://github.com/Pivotal-Field-Engineering/pcf-ers-demo.git
$ cd pcf-ers-demo/
$ ./mvnw clean install
```

**Prerequisites**
- [Cloud Foundry CLI](http://info.pivotal.io/p0R00I0eYJ011dAUCN06lR2)
- [Git Client](http://info.pivotal.io/i1RI0AUe6gN00C010l12J0R)
- An IDE, like [Spring Tool Suite](http://info.pivotal.io/f00RC0N0lh01eU21IAJ260R)
- [Java SE Development Kit](http://info.pivotal.io/n0I60i3021AN0JU0le10CRR)

### To run the application locally
The application is set to use an embedded H2 database in non-PaaS environments, and to take advantage of Pivotal CF's auto-configuration for services. To use a MySQL Dev service in PCF, simply create and bind a service to the app and restart the app. No additional configuration is necessary when running locally or in Pivotal CF.

In Pivotal CF, it is assumed that a Pivotal MySQL service will be used.

```
$ ./mvnw spring-boot:run
```

Then go to the http://localhost:8080 in your browser

## Demo Scripts summary
Just enough to run the demo, for detail follow the demo documentation.

#### **CF PUSH [doc](docs/demo_01.adoc) : - Deploying an application**
This demo is the very first introduction to PCF. First you walk the customer thru Pivotal Apps Manager, then CF CLI where you'll deploy the application.

  1. Explain the CF CLI in particular the concept of API and TARGET (connect with your Pivotal Apps Manager walk-thru)
    * cf api - change if necessary
    * cf login - it may prompt for org and space
  1. Explain the application deployment process (staging, buildpack, manifest)  
    * cf push - manifest.yml name and path may need to be adjusted 
  1. Show the application 
    * cf app [attendees] - unless name changed
    * Show in Apps Manager and Access it via route

