The `.openshift` folder will contain all the resources for building and
deploying your application

#### Build Config:

A build configuration is defined by a BuildConfig, which is a REST object that can be used in a POST to the API server to create a new instance.
The build config template is located at
.openshift/templates/build.yml. We want to keep the build config
template generic and use parameters to fulfill the template. The param
file that is used to build the application in the sbx environment is
located at .openshift/params/build-sbx.

#### Deploy Config:

A deployment configuration consists of the following key parts:
1. A replication controller template which describes the application to be deployed.
2. The default replica count for the deployment.
3. A deployment strategy which will be used to execute the deployment.
4. A set of triggers which cause deployments to be created automatically.

The Deploy Config is located at .openshift/templates/deploy.yml. We
want to keep the deploy config as generic as possible. All the params
for the deploy.yml are located at .openshift/params/deploy-sbx.