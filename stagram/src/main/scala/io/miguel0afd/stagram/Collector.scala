package io.miguel0afd.stagram

import com.rydgel.scalagram._
import com.rydgel.scalagram.responses._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// You are still able to perform a synchronous call for quick and dirty stuff
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by miguelangelfernandezdiaz on 26/01/16.
  */
class Collector extends App {

  val clientId = "client-id"
  val clientSecret = "client-secret"
  val redirectURI = "redirect-URI"

  // Server-Side login
  // Step 1: Get a URL to call. This URL will return the CODE to use in step 2
  val codeUrl = Authentication.codeURL(clientId, redirectURI)

  // Step 2: Use the code to get an AccessToken
  val accessTokenFuture = Authentication.requestToken(clientId, clientSecret, redirectURI, code = "the-code-from-step-1")
  val accessToken = accessTokenFuture onComplete {
    case Success(Response(Some(token: AccessToken), _, _, _)) => token
    case Failure(t) => println("An error has ocurred: " + t.getMessage)
  }

  // Making an authenticated call
  val auth = AccessToken("an-access-token")
  // The library is asynchronous by default and returns a promise.
  val future = Scalagram.userFeed(auth)
  future onComplete {
    case Success(Response(data, pagination, meta, headers)) => println(data) // do stuff
    case Failure(t) => println("An error has ocurred: " + t.getMessage)
  }

  val response: Response[List[Media]] = Await.result(Scalagram.userFeed(auth), 10 seconds)

  // Enforce signed parameters
  // You can activate this option for all your calls
  // You just need to create a SignedAccessToken instead.
  // (please read the documentation here https://instagram.com/developer/secure-api-requests/)
  val signedAccessToken = SignedAccessToken("accessToken", "clientSecret")
  // Usage example
  Scalagram.comment(signedAccessToken, "media-id", "my comment")

}
