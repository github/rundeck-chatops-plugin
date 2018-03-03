# rundeck-chatops-plugin
Rundeck plugin which sends a chatops RPC API call
-----------------------------------------------------------------

Based on [rundeck-httppost-plugin](https://github.com/rvs-fluid-it/rundeck-httppost-plugin)


Requires the following env vars set:

* `CHATOPS_AUTH_TOKEN` -- HTTP Basic Auth password
* `CHATOPS_PRIVATE_KEY` -- RSA SHA256 Private Key used to sign requests
