package io.miguel0afd.stagram.collector.config

import com.typesafe.config.{ConfigFactory, Config}

/**
  * Created by miguelangelfernandezdiaz on 26/01/16.
  */
object CollectorConfig {
  val CredentialsFile = "credentials.conf"
  val Root = "stagram"
}

trait CollectorConfig {

  import CollectorConfig._

  val config: Config = {
    ConfigFactory.load(CredentialsFile).getConfig(Root)
  }

}
