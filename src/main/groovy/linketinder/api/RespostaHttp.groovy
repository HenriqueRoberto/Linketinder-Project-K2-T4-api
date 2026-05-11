package linketinder.api

import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonOutput

class RespostaHttp {

    static void enviar(HttpExchange exchange, int status, Object corpo) {
        String json = JsonOutput.toJson(corpo)
        byte[] bytes = json.getBytes("UTF-8")
        exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)
        exchange.responseBody.withCloseable { it.write(bytes) }
    }

    static void erro(HttpExchange exchange, int status, String mensagem) {
        enviar(exchange, status, [erro: mensagem])
    }
}