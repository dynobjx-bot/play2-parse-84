package plugins

import play.api.{Application, Plugin}

class ParsePlugin(app: Application) extends Plugin {
  lazy val parseAPI: ParseAPI = {
    new ParseAPI(
      app.configuration.getString("parse.core.appId").get,
      app.configuration.getString("parse.core.restKey").get
    )
  }

  override def onStart() = {
    parseAPI
  }
}
