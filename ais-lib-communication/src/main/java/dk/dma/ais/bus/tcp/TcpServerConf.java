/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.ais.bus.tcp;

/**
 * TCP server configuration
 */
public class TcpServerConf {

    private int port = 8090;
    private int maxClients = 1000;

    /**
     * Instantiates a new Tcp server conf.
     */
    public TcpServerConf() {

    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets max clients.
     *
     * @return the max clients
     */
    public int getMaxClients() {
        return maxClients;
    }

    /**
     * Sets max clients.
     *
     * @param maxClients the max clients
     */
    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

}
