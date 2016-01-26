package io.miguel0afd.stagram.collector

import com.rydgel.scalagram._
import com.rydgel.scalagram.responses._
import io.miguel0afd.stagram.collector.config.CollectorConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// You are still able to perform a synchronous call for quick and dirty stuff
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by miguelangelfernandezdiaz on 26/01/16.
  */
class Collector extends App with CollectorConfig {

  val clientId = config.getString("clientId")
  val clientSecret = config.getString("clientSecret")
  val redirectURI = config.getString("redirectURI")

  // Server-Side login
  // Step 1: Get a URL to call. This URL will return the CODE to use in step 2
  val codeUrl = Authentication.codeURL(clientId, redirectURI)

  // Step 2: Use the code to get an AccessToken
  val accessTokenFuture = Authentication.requestToken(clientId, clientSecret, redirectURI, code = codeUrl)

  var authToken: AccessToken = ???

  val accessToken = accessTokenFuture onComplete {
    case Success(Response(Some(token: AccessToken), _, _, _)) => authToken = token
    case Failure(t) => println("An error has ocurred: " + t.getMessage)
  }

  // Making an authenticated call
  val auth = authToken
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
  val signedAccessToken = SignedAccessToken(authToken.token, clientSecret)
  // Usage example
  Scalagram.comment(signedAccessToken, "media-id", "my comment")

}
