package plugins

import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.ws.WS

class ParseAPI(appId: String, restKey: String) {
  private val PARSE_API_HEADER_APP_ID = "X-Parse-Application-Id"
  private val PARSE_API_HEADER_REST_API_KEY = "X-Parse-REST-API-Key"
  private val PARSE_API_URL = "https://api.parse.com"
  private val PARSE_API_URL_CLASSES = "/1/classes/"
  private val PARSE_API_HEADER_CONTENT_TYPE = "Content-Type"
  private val CONTENT_TYPE_JSON = "application/json; charset=utf-8"

  private val parseBaseUrl = "%s%s".format(PARSE_API_URL, PARSE_API_URL_CLASSES)

  def list(className: String) = parseWS(className).get()

  def create(className: String, json: JsValue) = {
    parseWS(className)
      .withHeaders(PARSE_API_HEADER_CONTENT_TYPE -> CONTENT_TYPE_JSON)
      .post(json)
  }

  private def parseWS(className: String) = WS.url("%s%s".format(parseBaseUrl, className))
    .withHeaders(PARSE_API_HEADER_APP_ID -> appId)
    .withHeaders(PARSE_API_HEADER_REST_API_KEY -> restKey)
}
