package pers.kelvin.util.ssh;

import com.jcraft.jsch.JSchException;
import org.testng.annotations.Test;

import java.io.IOException;

public class SSHTelnetClientTest {

    @Test
    public void testInvokeDubboUat() throws IOException, JSchException {
        String host = "";
        int port = 22;
        String userName = "";
        String password = "";
        String remoteHost = "";
        String remotePort = "";
        SSHTelnetClient telnetClient = new SSHTelnetClient(host, port, userName, password,
                "UTF-8", 3000);
        telnetClient.telnetDubbo(remoteHost, remotePort);
        System.out.println(
                telnetClient.invokeDubbo("", ""));
        telnetClient.disconnect();
    }

    @Test
    public void testInvokeDubboSit() throws IOException, JSchException {
        String host = "";
        int port = 22;
        String userName = "";
        String password = "";
        String secretKey = "";
        String remoteHost = "";
        String remotePort = "";
        SSHTelnetClient telnetClient = new SSHTelnetClient(host, port, userName, password, secretKey,
                "UTF-8", 3000);
        telnetClient.telnetDubbo(remoteHost, remotePort);
        System.out.println(
                telnetClient.invokeDubbo("", ""));
        telnetClient.disconnect();
    }

}