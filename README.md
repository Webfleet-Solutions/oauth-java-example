# OAuth Java Example

This sample code shows a Java Spring Web application accessing Webfleet Solutions APIs using [OAuth 2.0 authorization code flow](https://tools.ietf.org/html/rfc6749#section-1.3.1).

[![travis](https://api.travis-ci.org/Webfleet-Solutions/oauth-java-example.svg?branch=master)]()
[![license](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://opensource.org/licenses/MIT)

## Description

### Features

This application features:

- Authenticate an [OAuth 2.0 client](https://tools.ietf.org/html/rfc6749#section-1.1)
- Consume a Webfleet Solutions API
- Refresh access token  
- Web interface 
- Form based authentication

> **Note**
> 
> This project is not production ready, lacks proper security mechanisms. 
> Secure channel communication, safe data storage and others are out of the scope for this project.

### Client authentication

Flow starts on _/linkAccount_ endpoint [_LinkAccountController_](./src/main/java/com/webfleet/oauth/controller/LinkAccountController.java), 
simply redirecting the user-agent (browser) to Webfleet Solutions Authserver authorization endpoint indicating the flow to follow (**response_type=code**).

Authorization request example triggering user's authentication:

|Parameter|Mandatory| Description|
|---------|---------|------------|
|**response_type**|:white_check_mark:|OAuth 2.0 grant flow, we are using **code** to signal we are triggering an [authorization code flow](https://tools.ietf.org/html/rfc6749#section-1.3.1)|
|**client_id**|:white_check_mark:|Client identifier provided by Webfleet solutions during registration process|
|**redirect_uri**|:white_check_mark:|Callback uri user-agent should be redirected to after successful authentication|
|**state**|:white_check_mark:|Random value used for validation during callback request|
|scopes||Scopes parameter with requesting scopes. Optional, specific APIs documentation |

```http
GET /uaa/oauth/authorize?scope=<YOUR_SCOPES>&redirect_uri=<YOUR_REDIRECT_URI>&client_id=<YOUR_CLIENT_ID>&response_type=code&state={random} HTTP/1.1
Host: auth.webfleet.com
```

Replacing placeholders with the appropriate values the request will trigger user authentication on Webfleet Solutions Authserver.

![Webfleet Solutions Login Page](./assets/login_page.png) 

Once the user has authenticated its user-agent will be redirected to the indicated uri in the **redirect_uri** parameter. 
In this sample it points to the [_CallbackController_](./src/main/java/com/webfleet/oauth/controller/CallbackController.java). 
Modify _webfleet.redirecturi_ setting in [application.yml](./src/main/resources/application.yml) to match your client's settings.

Webfleet Solutions Authserver will add a an authorization code as query parameter (**code**) to the callback uri which can be used to obtain access and refresh tokens.

> **Note**
> 
> For security reasons **authorization code** can only be __exchanged once__ and has a __validity of 10min__. (600 seconds).

### Obtaining access and refresh tokens

To obtain access and refresh tokens we need to exchange the previously received authorization code after a successful user authorization. 
This is shown in the _/callback_ endpoint implemented in [_CallbackController_](./src/main/java/com/webfleet/oauth/controller/CallbackController.java).

Authorization code exchange request example: 

```http
POST /uaa/oauth/token HTTP/1.1
Host: auth.webfleet.com
Content-type: application/x-www-form-urlencoded
Accept: application/json

grant_type=authorization_code&client_id=<YOUR_CLIENT_ID>&client_secret=<YOUR_CLIENT_SECRET>&code={code}&redirect_uri=<YOUR_REDIRECT_URI>
```

|Parameter|Mandatory| Description|
|---------|---------|------------|
|**grant_type**|:white_check_mark:| OAuth 2.0 grant flow, we are using **authorization_code** following [authorization code flow](https://tools.ietf.org/html/rfc6749#section-1.3.1)|
|**client_id**|:white_check_mark:|Client identifier provided by Webfleet solutions during registration process|
|**client_secret**|:white_check_mark:|Client secret provided by Webfleet solutions during registration process|
|**code**|:white_check_mark:|Authorization code obtained during the authentication process |
|**redirect_uri**|:white_check_mark:|Redirect uri associated to the OAuth client provided during the partner registration process|


A successful response from the previous request would be something like: 

```json
{
  "access_token": "string",
  "token_type": "bearer",
  "refresh_token": "string",
  "expires_in": int,
  "scope": "string",
  "services": [],
  "jti": "string"
}
``` 

- **access_token** : Used to authorize Webfleet Solutions APIs requests
- **refresh_token** : Used to obtain a new **access_token**, **MUST** be stored safely (recommendation: use symmetric encryption to persist it)
- **token_type** : How this token is used to authenticate requests. Default 'bearer' meaning must be informed in any request to authenticated resource. 
For other implementations see OAuth spec
- **expires_in** : Access token expiration time duration in seconds
- **scope** : Scopes granted to the provided access token
- **services** : Custom property carrying Webfleet Solutions information
- **jti** : Access token identifier

> **Note** 
> 
> **Beware access and refresh tokens have their own expiration times.**
> 
> Typically **access tokens are short lived** being valid for just a few hours, while **refresh tokens are long lived** and may have an expiration time of months or years.
> 
> **Refresh tokens MUST be stored in a secure place**. This token can be exchanged for a new access token so effectively 
> granting access to user's data. In case of leak, please notify Webfleet Solutions as soon as possible.

### Requesting Webfleet Solutions API

A valid not expired access token to consume a Webfleet Solutions API, simply it must be informed using an _Authorization_ header.

```http
GET /api HTTP/1.1
...
Authorization: Bearer <ACCESS_TOKEN>
...
```

![Access token](./assets/access_token.png)

This token may have expired or may expire during the process, especially if this process spans over multiple requests to Webfleet Solutions APIs, in such cases use a refresh token previously stored to obtain a new access token. This process is covered by the next section.

### Refreshing access token using a refresh token

Once an access token has expired, a refresh token can be used to request a new access token without requesting the resource owner to authenticate again.

This is shown in _/refresh_ endpoint of [_RefreshTokenController_](./src/main/java/com/webfleet/oauth/controller/RefreshTokenController.java)

![Refresh token](./assets/refresh_token.png)

This is similar to the previous step in where we obtained an access token using an authorization code, remark there is no authorization code, instead we got a refresh token which we can use on Webfleet Solutions Authserver with a different flow, [OAuth 2.0 Refresh token grant flow](https://tools.ietf.org/html/rfc6749#section-6). 

```http
POST /uaa/oauth/token HTTP/1.1
Content-type: application/x-www-form-urlencoded
Accept: application/json

grant_type=refresh_token&client_id=<YOUR_CLIENT_ID>&client_secret=<YOUR_CLIENT_SECRET>&refresh_token={refresh_token}
``` 

A successful response will response something like:

```json
{
  "access_token": "string",
  "token_type": "bearer",
  "refresh_token": "string",
  "expires_in": int,
  "scope": "string",
  "services": [],
  "jti": "string"
}
``` 

> **Note**
> 
> Refreshing an access token returns a new **refresh_token**. 
> New refresh token must replace previously stored ones.
>

### Revoking refresh tokens

Revocation of refresh tokens is implemented following [OAuth 2.0 Token revocation (RFC7009)](https://tools.ietf.org/html/rfc7009). Given WFS Authserver uses [JSON Web Token specification (RFC7519)](https://tools.ietf.org/html/rfc7519) to issue signed self-contained tokens, only refresh tokens can be centrally revoked, access tokens stay valid until they have expired and cannot be revoked.

OAuth clients may revoke any refresh token issued to them, thus not requiring customer consent to revoke access to a customer granted refresh token.

Revoking a refresh token requires the following parameters in a form encoded request.

|Parameter|Mandatory|Description|
|---------|---------|-----------|
|__token__|:white_check_mark:|Refresh token to be revoked|

Example using [Basic authentication](https://tools.ietf.org/html/rfc7235)

```http
POST /uaa/oauth/revoke
Host: auth.webfleet.com
Authorization: Basic PHlvdXJfY2xpZW50X2lkPjo8eW91cl9jbGllbnRfc2VjcmV0PiA=
Content-Type: application/x-www-form-urlencoded

token=eyJhbGciOiJ...
```

A successful response will always return HTTP 200 OK status meaning the refresh token was revoked, for security reasons the response if the refresh token was not revoked  will keep being HTTP 200 OK.

## Setup

You must have contacted Webfleet Solutions before trying this application, follow the registration process which will provide you with a pair of OAuth client credentials. 
To fully run the example application you will also need a valid Webfleet Solutions subscription and user with the appropriate rights. 

**Available application credentials**

The application is secured using simple form based authentication. Find below the available credentials.
- **username**: admin
- **password**: password

Application users are hard coded and stored in memory, modify [WebSecurityConfig](./src/main/java/com/webfleet/oauth/config/WebSecurityConfig.java) to fit your needs.
  
### Configuration

Multiple settings can be tuned in [application.yml](src/main/resources/application.yml). 
By default the application starts a jetty instance listening on port 9080, this can be modified changing _server.port_ setting in [application.yml](src/main/resources/application.yml).

### Running

The application uses Spring Boot and gradle, to run the application using gradle 

```bash
./gradlew bootRun
```

Alternatively you may run it as a java application whose main class is **com.webfleet.oauth.Application**

> **Note**
> 
> WFS_CLIENT_ID and WFS_CLIENT_SECRET environment variables must be defined before executing the application.
>

### Docker 

You may also use the provided Docker descriptor to easily run the application without installing any other dependencies than Docker.
Replace your client credentials placeholders to successfully run the Docker container.

```bash
docker build -t oauth-java-example .
&& docker run
-p 9080:9080
--env WFS_CLIENT_ID=<YOUR_CLIENT_ID>
--env WFS_CLIENT_SECRET=<YOUR_CLIENT_SECRET>
--name oauth-java-example
--rm
oauth-java-example 
``` 

## License

This code is licensed under [MIT License.]((https://opensource.org/licenses/MIT)) 
