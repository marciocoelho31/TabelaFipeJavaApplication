package br.com.marciocoelho.TabelaFipe.principal;

import br.com.marciocoelho.TabelaFipe.model.Dados;
import br.com.marciocoelho.TabelaFipe.model.Modelos;
import br.com.marciocoelho.TabelaFipe.model.Veiculo;
import br.com.marciocoelho.TabelaFipe.service.ConsumoApi;
import br.com.marciocoelho.TabelaFipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversorDados = new ConverteDados();

    public void exibeMenu() {
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar.
                """ ;
        System.out.println(menu);
        var opcao = leitura.nextLine();

        String endereco = "";
        if (opcao.toLowerCase(Locale.ROOT).contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase(Locale.ROOT).contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        var marcas = conversorDados.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca para consulta:");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modelosLista = conversorDados.obterDados(json, Modelos.class);
        System.out.println("\nModelos dessa marca:");
        modelosLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do veículo a ser buscado:");
        var nomeVeiculo = leitura.nextLine();
        List<Dados> modelosFiltrados = modelosLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase(Locale.ROOT)
                        .contains(nomeVeiculo.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        System.out.println("\nModelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação:");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversorDados.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAno = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAno);
            Veiculo veiculo = conversorDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n\nVeículos filtrados com avaliações por ano:");
        veiculos.forEach(System.out::println);

    }
}
