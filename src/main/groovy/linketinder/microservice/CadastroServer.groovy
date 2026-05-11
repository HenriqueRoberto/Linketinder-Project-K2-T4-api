package linketinder.microservice

import com.sun.net.httpserver.HttpServer
import linketinder.dao.CandidatoDAO
import linketinder.dao.CompetenciaDAO
import linketinder.dao.EmpresaDAO
import linketinder.dao.VagaDAO
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService

class CadastroServer {

    private static final int PORTA = 8080

    static void iniciar() {
        def candidatoService = new CandidatoService(new CandidatoDAO())
        def empresaService   = new EmpresaService(new EmpresaDAO(), new VagaDAO(), new CompetenciaDAO())

        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0)
        server.createContext("/cadastro", new CadastroHandler(candidatoService, empresaService))
        server.setExecutor(null)
        server.start()
        println "Microserviço de cadastro rodando em http://localhost:${PORTA}"
        println "Rotas disponíveis:"
        println "  POST /cadastro/candidato"
        println "  POST /cadastro/empresa"
    }
}