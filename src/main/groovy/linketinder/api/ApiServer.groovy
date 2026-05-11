package linketinder.api

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

class ApiServer {

    private final int porta
    private HttpServer servidor

    ApiServer(int porta) {
        this.porta = porta
    }

    void registrar(String caminho, HttpHandler handler) {
        if (servidor == null) {
            servidor = HttpServer.create(new InetSocketAddress(porta), 0)
        }
        servidor.createContext(caminho, handler)
    }

    void iniciar() {
        if (servidor == null) {
            throw new IllegalStateException("Nenhum handler registrado antes de iniciar o servidor.")
        }
        servidor.setExecutor(null)
        servidor.start()
        println "API REST iniciada em http://localhost:" + porta
    }
}