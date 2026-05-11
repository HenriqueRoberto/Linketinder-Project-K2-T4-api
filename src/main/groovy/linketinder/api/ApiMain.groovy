package linketinder.api

import linketinder.api.handler.CandidatoHandler
import linketinder.controller.AppController

class ApiMain {

    static void main(String[] args) {
        AppController app = AppController.criar()

        ApiServer servidor = new ApiServer(8080)
        servidor.registrar("/candidatos", new CandidatoHandler(app.candidatoController))
        servidor.iniciar()
    }
}