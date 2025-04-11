package menu;

import conection.ConversorMoedaApi;
import models.Conversor;
import models.ParDeMoeda;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final ParDeMoeda[] paresDeMoedas = {
            new ParDeMoeda("Dólar Americano USD", "Peso Argentino ARS"),
            new ParDeMoeda("Peso Argentino ARS", "Dólar Americano USD"),
            new ParDeMoeda("Dólar Americano USD", "Real Brasileiro BRL"),
            new ParDeMoeda("Real Brasileiro BRL", "Dólar Americano USD"),
            new ParDeMoeda("Dólar Americano USD", "Peso Colombiano COP"),
            new ParDeMoeda("Peso Colombiano COP", "Dólar Americano USD")
    };

    private final List<String> registrosDeConversao = new ArrayList<>();

    public void displayMenu() {
        Scanner opcao = new Scanner(System.in);
        int escolha = 0;
        ConversorMoedaApi conversor = new ConversorMoedaApi();

        String menu = """
        *********************************************************
                   BEM VINDO(A) AO CONVERSOR DE MOEDAS
        *********************************************************
                    
        Escolha uma opção para realizar a conversão:
        """;

        do {
            try {
                System.out.println(menu);
                for (int i = 0; i < paresDeMoedas.length; i++) {
                    System.out.println((i + 1) + ". " +
                            paresDeMoedas[i].fromMoeda() + " para >>>> " + paresDeMoedas[i].toMoeda());
                }
                System.out.println(paresDeMoedas.length + 1 + ". Sair");
                System.out.println("\nOpção: ");

                escolha = opcao.nextInt();

                if (escolha > 0 && escolha <= paresDeMoedas.length) {
                    processaConversao(opcao, conversor, escolha - 1);
                    System.out.println("Deseja realizar outra conversão? (S/N): ");
                    opcao.nextLine(); // Consumir o restante da linha
                    String continuar = opcao.nextLine();
                    if (continuar.equalsIgnoreCase("n")) {
                        break;
                    }
                } else if (escolha != paresDeMoedas.length + 1) {
                    System.out.println("Opção inválida. Digite uma opção válida!");
                }
            } catch (Exception e) {
                System.out.println("Erro ao processar a entrada: " + e.getMessage());
                opcao.nextLine(); // Limpa entrada inválida
            }
        } while (escolha != paresDeMoedas.length + 1);

        System.out.println("Finalizando a aplicação...");
        exibirRegistrosDeConversao();
    }

    private void processaConversao(Scanner opcao, ConversorMoedaApi conversor, int indice) {
        try {
            System.out.println("Digite um valor: ");
            double valor = opcao.nextDouble(); // Captura o valor a ser convertido

            // Obtém o par de moedas selecionado
            ParDeMoeda par = paresDeMoedas[indice];

            // Extrai os códigos das moedas
            String fromCodigo = par.fromMoeda().substring(par.fromMoeda().lastIndexOf(" ") + 1);
            String toCodigo = par.toMoeda().substring(par.toMoeda().lastIndexOf(" ") + 1);

            // Realiza a conversão usando a API
            Conversor conversao = conversor.buscaConversor(fromCodigo, toCodigo, valor);

            if (conversao != null) {
                System.out.println("\nVocê selecionou: " + par.fromMoeda() + " para " + par.toMoeda() + ".");
                System.out.println("O valor de " + valor + " " + fromCodigo +
                        " convertido é " + conversao.conversion_result() + " " + toCodigo);
                System.out.println(" ");

                // Adiciona o registro ao histórico
                salvarRegistro(par, valor, conversao.conversion_result());
            } else {
                System.out.println("Erro na conversão. Verifique os dados e tente novamente.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar a conversão: " + e.getMessage());
        }
    }

    private void salvarRegistro(ParDeMoeda par, double valor, double resultadoConversao) {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String registro = String.format("Data: %s | Conversão: %.2f %s para %.2f %s",
                agora.format(formatador),
                valor,
                par.fromMoeda().substring(par.fromMoeda().lastIndexOf(" ") + 1),
                resultadoConversao,
                par.toMoeda().substring(par.toMoeda().lastIndexOf(" ") + 1));

        registrosDeConversao.add(registro);
    }

    private void exibirRegistrosDeConversao() {
        System.out.println("\nHistórico de Conversões:");
        if (registrosDeConversao.isEmpty()) {
            System.out.println("Nenhuma conversão realizada.");
        } else {
            for (String registro : registrosDeConversao) {
                System.out.println(registro);
            }
        }
    }
}
