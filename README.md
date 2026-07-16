# 🔴 Pokédex - MC322 (Projeto Final)

> Um sistema robusto de gerenciamento, catalogação e análise de Pokémon desenvolvido em Java para a disciplina **MC322 (Programação Orientada a Objetos)** da UNICAMP. O projeto une conceitos avançados de POO, persistência segura em arquivos, regras de negócio dinâmicas para vantagens/desvantagens de tipos e uma suíte completa de testes unitários automatizados.

---

## 🛠️ Arquitetura do Sistema e Design Patterns

O projeto foi estruturado seguindo rigorosamente a separação de responsabilidades (camadas), o que garante alta coesão e baixo acoplamento:

pokedex
├── abstracts      # Classes abstratas de persistência base (reusabilidade)
├── enums          # Tipagens de Pokémon, Sexos e Relações Evolutivas
├── exceptions     # Exceções customizadas do domínio (ex: duplicidade ou inexistência)
├── interfaces     # Contratos estruturais do sistema
├── model          # Classes de dados puras (Entidades como Pokemon e Estatisticas)
├── repository     # Lógica de manipulação e persistência de dados em arquivos
├── service        # Orquestrador das regras de negócio (Cálculos de vantagens, validações)
└── ui             # Interface de Linha de Comando interativa (CLI)

---

## 🚀 Principais Funcionalidades

* **Cadastro Inteligente de Pokémon**: Registro completo contendo nome, peso, altura, gênero, tipagem primária/secundária e estatísticas base de combate (HP, Attack, Defense, Sp. Atk, Sp. Def, Speed).
* **Edição de Dados em Tempo Real**: Tela interativa que permite modificar qualquer atributo do Pokémon cadastrado, apresentando uma tela de revisão antes de persistir as mudanças no banco de dados flat-file.
* **Cálculo Automático de Batalha (Matchup)**: Geração automatizada das fraquezas e resistências elementais do Pokémon ao cadastrá-lo, utilizando um algoritmo de matriz de eficiência de tipos integrado no `CalculadoraTipo`.
* **Consulta e Busca Avançada**: Pesquisas instantâneas por ID (Número de catalogação) ou nome, exibindo árvores evolutivas e relações de parentesco entre espécies.
* **Mecanismo de Persistência Flat-File**: Criação de um banco de dados em arquivo de texto plano customizado com controle de concorrência e geração automática de IDs incrementais (`proximoNumero`).

---

## 🧪 Qualidade de Software: Testes Unitários e Cobertura (83%)

Para atingir o padrão de excelência exigido na disciplina, desenvolvemos testes automatizados utilizando **JUnit 5** e medimos a eficiência de cobertura de ramificações de código através do **JaCoCo**. 

Ao ignorar a camada visual (`pokedex.ui`), que depende exclusivamente de inputs de teclado do usuário, o motor de testes cobriu de forma massiva as regras lógicas do programa:

| Pacote / Elemento | Cobertura de Instruções | Status |
| :--- | :---: | :---: |
| **`pokedex.service`** | **99%** | 🟢 Excelente |
| **`pokedex.repository`** | **82%** | 🟢 Excelente |
| **`pokedex.model`** | **~80%** | 🟢 Excelente |
| **`pokedex.exceptions`** | **100%** | 🟢 Excelente |
| **COBERTURA TOTAL (Geral)** | **83%** | 🟢 Excelente |

---

## 🎮 Como Executar o Projeto

Você pode buildar, rodar e testar o projeto facilmente utilizando o Gradle Wrapper integrado:

### 1. Inicializar a Pokédex (Interface CLI):
Windows PowerShell: ./gradlew run
Linux / macOS: ./gradlew run

### 2. Executar a Suíte de Testes Automatizados:
Comando: ./gradlew test

### 3. Visualizar o Relatório Gráfico do JaCoCo:
Após executar os testes, o relatório detalhado de cobertura estará disponível em formato HTML. Para visualizá-lo, abra o seguinte arquivo em seu navegador de preferência:
app/build/reports/jacoco/test/html/index.html

---

## 👥 Desenvolvedores (Grupo)

* **Leonardo Batista da Silva RA 223493** - UNICAMP / MC322
* **RA 294788** - UNICAMP / MC322

---
*Este projeto foi desenvolvido estritamente para fins pedagógicos e de avaliação na Universidade Estadual de Campinas (UNICAMP).*
