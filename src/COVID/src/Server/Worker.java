package COVID.src.Server;

import COVID.src.Exceptions.AccountExceptions.InvalidAcount;
import COVID.src.Coronita.Interface;
import COVID.src.Exceptions.*;
import COVID.src.Exceptions.AccountExceptions.InvalidUsername;
import COVID.src.Exceptions.AccountExceptions.InvalidUsername;
import COVID.src.Exceptions.PasswordExceptions.MismatchPassException;
import COVID.src.Server.Exceptions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Worker implements Runnable, Interface {
    Socket client;
    BufferedReader in;
    PrintWriter out;
    String idCliente;
    Estimate estimate;
    Accounts accounts;

    public Worker(Socket client, Estimate estimate, Accounts accounts) {
        try {
            client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.estimate = estimate;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        System.out.println("Começou");
        Writer writer = new Writer(out, estimate, idCliente);
        String read = null;
        try {
            read = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Vrmmmmm!");
        while (!(read.equals("quit")) || read != null) {
            String[] readParts = read.split("\\s+", 2);
            String command = readParts[0];
            System.out.println(command);
            String[] args;
            switch (command) {
                case "cr":
                    System.out.println("Reconheceu!");
                    args = readParts[1].split("\\s+");
                    try {
                        registerAccount(args[0], args[1]);
                    } catch (InvalidUsername invalidUsername) {
                        out.println(invalidUsername);
                        out.flush();
                    } catch (PasswordException e) {
                        out.println("Erro password");
                        out.flush();
                    } catch (CoronitaRemotException e) {
                    }
                case "lg":
                    args = readParts[1].split("\\s+");
                    try {
                        authenticate(args[0], args[1]);
                        writer.start();
                    } catch (InvalidUsername invalidUsername) {
                        out.println(invalidUsername);
                        out.flush();
                    } catch (PasswordException e) {
                        out.println("Erro password");
                        out.flush();
                    } catch (CoronitaRemotException e) {
                    }
                case "up":
                    args = readParts[1].split("\\s+");
                    try {
                        updateEstimate(Integer.parseInt(args[0]));
                    } catch (InvalidNumCases invalidNumCases) {
                        invalidNumCases.printStackTrace();
                    } catch (InvalidAcount invalidAcount) {
                        invalidAcount.printStackTrace();
                    }
                case "rm":
                    args = readParts[1].split("\\s+");
                    try {
                        removeAccount(args[0], args[1]);
                    } catch (InvalidAcount invalidAcount) {
                        out.println("Account exists");
                        out.flush();
                    } catch (PasswordException e) {
                        out.println("Erro Password");
                        out.flush();
                    } catch (InvalidUsername invalidUsername) {
                        out.println(invalidUsername);
                        out.flush();
                    }
                default:
                    System.out.println("E?");
                    out.println("És psycho!");
                    out.flush();
            }
            try {
                read = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            (writer.stop()).join(); //writer.stop() devolve a thread
            client.shutdownInput();
            client.shutdownOutput();
            client.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override

    public void registerAccount(String id, String passwd) throws InvalidAcount, MismatchPassException, InvalidUsernameServer {


        @Override
        public void authenticate (String id, String passwd) throws
        InvalidUsername, PasswordException, CoronitaRemotException {
            accounts.checkPasswd(id, passwd);
        }

        @Override
        public void removeAccount (String id, String passwd) throws InvalidUsername, InvalidAcount, PasswordException {
            accounts.removeAccount(id, passwd);
        }

        @Override
        public void updateEstimate ( int cases) throws InvalidNumCases, InvalidAcount {
            estimate.update(idCliente, cases);
        }
    }
}
