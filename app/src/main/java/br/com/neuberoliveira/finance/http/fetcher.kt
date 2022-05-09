package br.com.neuberoliveira.finance.http

import br.com.neuberoliveira.finance.BuildConfig
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

val BASE_URL = BuildConfig.BASE_AUTH_URL
fun fetcher(
  queue: RequestQueue,
  endpoint: String,
  method: Int,
  cb: (response: JSONObject, status: Int) -> Unit
) {
  val stringRequest = StringRequest(
    method, BASE_URL + endpoint,
    { response ->
      cb(JSONObject(response), 200)
    },
    { err ->
      val responseStr = String(err.networkResponse.data)
      cb(JSONObject(responseStr), err.networkResponse.statusCode)
    })
  queue.add(stringRequest)
}

fun hashToQueryString(params: HashMap<String, String>): String {
  val query = mutableListOf<String>()
  params.forEach { (key, value) -> query.add("$key=$value") }
  
  return query.joinToString("&")
}