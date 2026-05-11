package linketinder.api.handler

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper
import linketinder.api.RespostaHttp
import linketinder.controller.EmpresaController
import linketinder.model.Empresa

class EmpresaHandler implements HttpHandler {

    private final EmpresaController empresaController

    EmpresaHandler(EmpresaController empresaController) {
        this.empresaController = empresaController
    }

    @Override
    void handle(HttpExchange exchange) {
        if (exchange.requestMethod != "POST") {
            RespostaHttp.erro(exchange, 405, "Método não permitido.")
            return
        }

        try {
            String corpo = exchange.requestBody.getText("UTF-8")
            Map dados = new JsonSlurper().parseText(corpo) as Map
            Empresa empresa = empresaController.cadastrar(dados)
            RespostaHttp.enviar(exchange, 201, [id: empresa.id, email: empresa.email])
        } catch (IllegalArgumentException e) {
            RespostaHttp.erro(exchange, 400, e.message)
        } catch (Exception e) {
            RespostaHttp.erro(exchange, 500, "Erro interno: " + e.message)
        }
    }
}