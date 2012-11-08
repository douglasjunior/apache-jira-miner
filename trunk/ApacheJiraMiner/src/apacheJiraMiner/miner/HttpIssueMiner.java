/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.miner;

import apacheJiraMiner.pojo.*;
import apacheJiraMiner.util.Connection;
import apacheJiraMiner.util.Util;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Douglas
 */
public class HttpIssueMiner {

    private int numeroProximaPagina;
    private Projeto projeto;
    private String logFile;
    private int inexistentes;
    private boolean minerarComentarios;
    private boolean minerarCommits;
    private Date dataInicial;
    private boolean codificarStrings;
    private boolean filtrarStrings;
    private static final String[][] meses = new String[][]{{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}, {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"}};

    /**
     * Construtor padrão e privado pois é obrigatória a informação do Projeto
     * desejado.
     */
    private HttpIssueMiner() {
        this.logFile = "log/";
        this.inexistentes = 0;
        this.numeroProximaPagina = 0;
        this.minerarComentarios = true;
        this.minerarCommits = true;
        this.codificarStrings = true;
        this.filtrarStrings = true;
    }

    /**
     * Construtor base.
     *
     * @param projeto - Objeto referente ao Projeto que deseja minerar as issues
     * com commits e comentarios.
     */
    public HttpIssueMiner(Projeto projeto) {
        this();
        this.logFile += projeto.getxKey() + ".txt";
        this.projeto = projeto;
        this.dataInicial = null;
    }

    /**
     * Construtor.
     *
     * @param projeto - Objeto referente ao Projeto que deseja minerar as
     * issues.
     * @param numeroProximaPagina - Número da issue inicial desejada.
     * @param minerarComentarios - para minerar os comentarios selecione TRUE.
     * @param minerarCommits
     */
    public HttpIssueMiner(Projeto projeto, int numeroProximaPagina, boolean minerarComentarios, boolean minerarCommits) {
        this(projeto);
        this.numeroProximaPagina = numeroProximaPagina;
        this.minerarComentarios = minerarComentarios;
        this.minerarCommits = minerarCommits;
    }

    /**
     * Construtor.
     *
     * @param projeto - Objeto referente ao Projeto que deseja minerar as
     * issues.
     * @param numeroProximaPagina - Número da issue inicial desejada.
     * @param minerarComentarios - para minerar os comentarios selecione TRUE.
     * @param minerarCommits
     */
    public HttpIssueMiner(Projeto projeto, Date dataInicial, boolean minerarComentarios, boolean minerarCommits) {
        this(projeto);
        this.minerarComentarios = minerarComentarios;
        this.minerarCommits = minerarCommits;
        this.dataInicial = dataInicial;
    }

    /**
     * Atualizar somente as datas das Issues que ja foram mineradas.
     *
     * @throws Exception
     */
    public void atualizarDatas() throws Exception {
        atualizarDatasDasIssuesDoProjeto(0);
    }

    public void atualizarDadosDasIssuesDoProjetoAPartirDeUmaData(int issueInicial) throws Exception {
        numeroProximaPagina = issueInicial;
        if (dataInicial == null) {
            System.err.println("Atributo 'dataInicial' não pode ser 'null'.");
            System.exit(1);
        }
        System.out.println("");
        System.out.println("-----------------------------------------");
        System.out.println("Iniciando a atualização das Issues do Projeto " + projeto.getxKey() + " a partir de " + dataInicial);
        System.out.println("-----------------------------------------\n");
        Util.writeToFile(logFile, "Início da mineração: " + new Date() + "\n");

        int contadorGC = 0;

        while (inexistentes <= 40) {
            if (contadorGC >= 50) {
                System.gc();
                contadorGC = 0;
            }
            System.out.println("---- Conectando a URL : " + getUrl());
            URL urlComentarios = new URL(getUrl() + "?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#issue-tabs");
            URL urlCommmits = new URL(getUrl() + "?page=com.atlassian.jira.plugin.ext.subversion:subversion-commits-tabpanel#issue-tabs");
            BufferedReader disComentarios = Util.abrirStream(urlComentarios);
            BufferedReader disCommits = Util.abrirStream(urlCommmits);
            System.out.println("---- Conectado a URL : " + getUrl());
            lerPaginasHtmlsEAtualizarIssuesExistentes(Util.capturarCodigoHtml(disComentarios), Util.capturarCodigoHtml(disCommits));
            disComentarios.close();
            disCommits.close();
            urlComentarios = null;
            urlCommmits = null;
            disComentarios = null;
            disCommits = null;
            contadorGC++;
        }
        Util.writeToFile(logFile, "Fim da mineração: " + new Date() + "\n");
        System.out.println("-----------------------------------------");
        System.out.println("Terminado a atualização das Issues do Projeto " + projeto.getxKey());
        System.out.println("-----------------------------------------\n");

        projeto = null;
    }

    /**
     * Atualizar somente as datas das Issues que ja foram mineradas.
     *
     * @param numeroIssueInicial - Número da issue inicial desejada
     * @throws Exception
     */
    public void atualizarDatasDasIssuesDoProjeto(int numeroIssueInicial) throws Exception {
        this.logFile += ".issuedata.txt";

        System.out.println("");
        System.out.println("----------------------------------------------");
        System.out.println("Iniciando a mineração das Datas das Issues");
        System.out.println("----------------------------------------------\n");
        Util.writeToFile(logFile, "Início da mineração: " + new Date() + "\n");

        int contadorGC = 0;

        for (Issue issue : projeto.getIssues()) {
            if (issue.getNumeroIssue() >= numeroIssueInicial) {
                if (contadorGC >= 50) {
                    contadorGC = 0;
                    System.gc();
                }

                this.numeroProximaPagina = issue.getNumeroIssue();

                System.err.println("--------- Iniciando a mineração das Datas da Issues ---------");
                System.err.println("Issue: " + getUrl());
                System.err.println("---------------------------------------------------------------\n");

                System.out.println("---- Conectando a URL : " + getUrl());
                URL urlComentarios = new URL(getUrl() + "?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#issue-tabs");

                BufferedReader disCommits = Util.abrirStream(urlComentarios);
                System.out.println("---- Conectado a URL : " + getUrl());

                List<String> linhas = Util.capturarCodigoHtml(disCommits);
                for (int i = linhas.size() - 300; i < linhas.size(); i++) {
                    pegarDadosIssue(issue, linhas, i);
                }
                if (Connection.dao.atualiza(issue)) {
                    Util.writeToFile(logFile, "Datas minerados com sucesso da issue: " + issue.getNumeroIssue());
                } else {
                    Util.writeToFile(logFile, "*Erro ao minerar Data da issue: " + issue.getNumeroIssue() + "*");
                }
                disCommits.close();

                System.err.println("--------- Concluido a mineração das Datas da Issues ---------");
                System.err.println("Issue: " + getUrl());
                System.err.println("---------------------------------------------------------------\n");

                disCommits = null;
                urlComentarios = null;
                issue = null;

                contadorGC++;
            }
        }

        Util.writeToFile(logFile, "Fim da mineração: " + new Date() + "\n");
        System.out.println("----------------------------------------------");
        System.out.println("Terminado a mineração dos Commits das Issues");
        System.out.println("----------------------------------------------\n");
        projeto = null;
    }

//    /**
//     * Atualizar somente as datas das Issues.
//     * @param issues - Lista com issues que deseja atualizar as datas.
//     * @throws MalformedURLException
//     * @throws Exception 
//     */
//    private void atualizarDatasDasIssues(List<Issue> issues) throws Exception {
//        int contadorGC = 0;
//        for (Issue issue : issues) {
//            if (contadorGC >= 50) {
//                contadorGC = 0;
//                System.gc();
//            }
//            this.projeto = issue.getProjeto();
//            if (projeto == null) {
//                Conn.daoProjeto.remove(issue);
//            } else {
//
//                this.logFile = "src/" + projeto.getxKey() + ".issuedata";
//
//                this.numeroProximaPagina = issue.getNumeroIssue();
//
//                System.err.println("--------- Iniciando a mineração das Datas da Issues ---------");
//                System.err.println("Issue: " + getUrl());
//                System.err.println("---------------------------------------------------------------\n");
//
//                System.out.println("---- Conectando a URL : " + getUrl());
//                URL urlComentarios = new URL(getUrl() + "?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#issue-tabs");
//
//                BufferedReader disCommits = Util.abrirStream(urlComentarios);
//                System.out.println("---- Conectado a URL : " + getUrl());
//
//                List<String> linhas = capturarCodigoHtml(disCommits);
//                for (int i = linhas.size() - 300; i < linhas.size(); i++) {
//                    pegarDadosIssue(issue, linhas, i);
//                }
//                if (Conn.daoProjeto.atualiza(issue)) {
//                    writeToFile(logFile, "Datas minerados com sucesso da issue: " + issue.getNumeroIssue());
//                } else {
//                    writeToFile(logFile, "*Erro ao minerar Data da issue: " + issue.getNumeroIssue() + "*");
//                }
//                disCommits.close();
//
//                System.err.println("--------- Concluido a mineração das Datas da Issues ---------");
//                System.err.println("Issue: " + getUrl());
//                System.err.println("---------------------------------------------------------------\n");
//
//                disCommits = null;
//                urlComentarios = null;
//                issue = null;
//            }
//            contadorGC++;
//        }
//    }
    /**
     * Método que atualiza somente os Commits de Issues que já foram mineradas.
     *
     * @throws Exception
     */
    public void atualizarCommitsDasIssues() throws Exception {
        atualizarCommitsDasIssues(0);
    }

    /**
     * Método que atualiza somente os Commits de Issues que já foram mineradas.
     *
     * @param numeroIssueInicial - Numero da Issue inicial já minerada
     * @throws Exception
     */
    public void atualizarCommitsDasIssues(int numeroIssueInicial) throws Exception {
        this.logFile += ".commit";

        System.out.println("");
        System.out.println("----------------------------------------------");
        System.out.println("Iniciando a mineração dos Commits das Issues");
        System.out.println("----------------------------------------------\n");
        Util.writeToFile(logFile, "Início da mineração: " + new Date() + "\n");

        int contadorGC = 0;

        for (Issue issue : projeto.getIssues()) {
            if (issue.getNumeroIssue() >= numeroIssueInicial) {
                if (contadorGC >= 50) {
                    contadorGC = 0;
                    System.gc();
                }

                this.numeroProximaPagina = issue.getNumeroIssue();

                System.err.println("--------- Iniciando a mineração dos Commits da Issues ---------");
                System.err.println("Issue: " + getUrl());
                System.err.println("---------------------------------------------------------------\n");

                System.out.println("---- Conectando a URL : " + getUrl());
                URL urlCommmits = new URL(getUrl() + "?page=com.atlassian.jira.plugin.ext.subversion:subversion-commits-tabpanel#issue-tabs");
                BufferedReader disCommits = Util.abrirStream(urlCommmits);
                System.out.println("---- Conectado a URL : " + getUrl());
                try {
                    lerCommits(issue, Util.capturarCodigoHtml(disCommits), true);
                    Util.writeToFile(logFile, "Commits minerados com sucesso da issue: " + issue.getNumeroIssue());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Util.writeToFile(logFile, "*Erro ao minerar commits da issue: " + issue.getNumeroIssue() + "*");
                }
                disCommits.close();

                System.err.println("--------- Concluido a mineração dos Commits da Issues ---------");
                System.err.println("Issue: " + getUrl());
                System.err.println("---------------------------------------------------------------\n");

                disCommits = null;
                urlCommmits = null;
                issue = null;

                contadorGC++;
            }
        }

        Util.writeToFile(logFile, "Fim da mineração: " + new Date() + "\n");
        System.out.println("----------------------------------------------");
        System.out.println("Terminado a mineração dos Commits das Issues");
        System.out.println("----------------------------------------------\n");

        projeto = null;
    }

    /**
     * Método que faz a mineração de todas as issues do projeto com commits e
     * comentarios.
     *
     * @throws Exception
     */
    public void minerarIssues() throws Exception {

        System.out.println("");
        System.out.println("-----------------------------------------");
        System.out.println("Iniciando a mineração das Issues");
        System.out.println("-----------------------------------------\n");
        Util.writeToFile(logFile, "Início da mineração: " + new Date() + "\n");

        int contadorGC = 0;

        while (inexistentes <= 40) {
            if (contadorGC >= 50) {
                System.gc();
                contadorGC = 0;
            }
            System.out.println("---- Conectando a URL : " + getUrl());
            URL urlComentarios = new URL(getUrl() + "?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#issue-tabs");
            BufferedReader disComentarios = Util.abrirStream(urlComentarios);
            BufferedReader disCommits = null;
            if (minerarCommits) {
                URL urlCommmits = new URL(getUrl() + "?page=com.atlassian.jirafisheyeplugin:fisheye-issuepanel#issue-tabs");
                disCommits = Util.abrirStream(urlCommmits);
            }
            System.out.println("---- Conectado a URL : " + getUrl());
            lerPaginasHtmlsESalvarNovasIssues(Util.capturarCodigoHtml(disComentarios), Util.capturarCodigoHtml(disCommits));
            disComentarios.close();
            if (minerarCommits) {
                disCommits.close();
            }
            urlComentarios = null;
            disComentarios = null;
            disCommits = null;
            contadorGC++;
        }

        Util.writeToFile(logFile, "Fim da mineração: " + new Date() + "\n");
        System.out.println("-----------------------------------------");
        System.out.println("Terminado a mineração das Issues");
        System.out.println("-----------------------------------------\n");

        projeto = null;
    }

    private void lerPaginasHtmlsEAtualizarIssuesExistentes(List<String> linhasComentarios, List<String> linhasCommits) throws Exception {
        Issue issue = Connection.consultaIssuePorNumeroEProjeto(numeroProximaPagina, projeto);
        boolean novaIssue = false;
        if (issue == null) {
            issue = lerNovaIssue(linhasComentarios);
            novaIssue = true;
        } else {
            issue = lerIssueExistente(issue, linhasComentarios);
            novaIssue = false;
        }

        if (issue != null && issue.getId() != 0) {
            if (minerarComentarios) {

                lerComentarios(issue, linhasComentarios, novaIssue);

            }
            if (minerarCommits) {

                lerCommits(issue, linhasCommits, novaIssue);

            }
        }
        issue = null;
        linhasComentarios = null;
        linhasCommits = null;
        numeroProximaPagina++;
    }

    private void lerPaginasHtmlsESalvarNovasIssues(List<String> linhasComentarios, List<String> linhasCommits) throws Exception {
        Issue issue = issue = lerNovaIssue(linhasComentarios);

        if (issue != null && issue.getId() != 0) {
            if (minerarComentarios) {
                lerComentarios(issue, linhasComentarios, true);
            }
            if (minerarCommits) {
                lerCommits(issue, linhasCommits, true);
            }
        }
        issue = null;
        linhasComentarios = null;
        linhasCommits = null;
        numeroProximaPagina++;
    }

    private Issue lerIssueExistente(Issue issue, List<String> linhas) {
        for (int i = 0; i < linhas.size(); i++) {
            if (!pegarDadosIssue(issue, linhas, i)) {
                return null;
            }
        }
        if (issue.getNome() != null) {
            inexistentes = 0;
            if (Connection.dao.atualiza(issue)) {
                System.err.println("-------- Issue atualizada --------");
                System.err.println("Nome: " + issue.getNome());
                System.err.println("Numero: " + issue.getNumeroIssue());
                System.err.println("----------------------------------------------------------\n");
                Util.writeToFile(logFile, "- Issue " + issue.getNumeroIssue() + " atualizada.");
                return issue;
            }
        }
        System.err.println("---------------- Erro ao atualizar Issue ----------------");
        System.err.println("Link: " + getUrl());
        System.err.println("Numero: " + numeroProximaPagina);
        System.err.println("----------------------------------------------------------\n");
        Util.writeToFile(logFile, "- Erro: Issue " + numeroProximaPagina + " não foi atualizada.");
        return null;
    }

    private Issue lerNovaIssue(List<String> linhas) {
        Issue issue = new Issue();
        issue.setNumeroIssue(numeroProximaPagina);
        for (int i = 0; i < linhas.size(); i++) {
            if (!pegarDadosIssue(issue, linhas, i)) {
                return null;
            }
        }
        if (issue.getNome() != null) {
            inexistentes = 0;
            boolean inseriu = false;
            projeto.addIssue(issue);
            if (Connection.dao.insere(issue)) {
                if (Connection.dao.atualiza(projeto)) {
                    System.err.println("-------- Issue cadastrado e adicionado ao Projeto --------");
                    System.err.println("Nome: " + issue.getNome());
                    System.err.println("Numero: " + issue.getNumeroIssue());
                    System.err.println("----------------------------------------------------------\n");
                    Util.writeToFile(logFile, "- Issue " + issue.getNumeroIssue() + " cadastrada e adicionado ao projeto.");
                } else {
                    projeto.removeIssue(issue);
                    System.err.println("----- Issue cadastrado e *NÃO* adicionado ao Projeto -----");
                    System.err.println("Nome: " + issue.getNome());
                    System.err.println("Numero: " + issue.getNumeroIssue());
                    Util.writeToFile(logFile, "- Issue " + issue.getNumeroIssue() + " cadastrada.");
                    System.err.println("----------------------------------------------------------\n");
                }
                return issue;
            }
            if (!inseriu) {
                System.err.println("---------------- Erro ao cadastrar Issue ----------------");
                System.err.println("Link: " + getUrl());
                System.err.println("Numero: " + numeroProximaPagina);
                System.err.println("----------------------------------------------------------\n");
                Util.writeToFile(logFile, "- Erro: Issue " + numeroProximaPagina + " não foi cadastrada.");
            }
        }
        return null;
    }

    private String pegaDescricao(List<String> linhas, int i) {
        StringBuilder sb = new StringBuilder();
        try {
            while (!linhas.get(i).contains("toggle-wrap") || linhas.get(i).contains("description-val")) {
                sb.append(Util.removeCodigoHTML(linhas.get(i), true));
                sb.append("\n");
                i++;
            }
            System.out.println("----------- Capturado Descrição da Issue ------------");
            System.out.println("Descrição: " + sb.toString());
            System.out.println("-----------------------------------------------------");

        } catch (Exception ex) {
            System.err.println("-------- Erro ao capturar Descrição da Issue --------");
            ex.printStackTrace();
            System.err.println("-----------------------------------------------------");
        }
        return sb.toString();
    }

    private boolean pegarDadosIssue(Issue issue, List<String> linhas, int i) {
        if (linhas.get(i).contains("<title>") && linhas.get(i).contains("Issue Does Not Exist")) {
            inexistentes++;
            System.err.println("---------------------------------------------\n");
            System.err.println("A página de Issue não existe");
            System.err.println("---------------------------------------------\n");
            Util.writeToFile(logFile, "- A Issue " + (numeroProximaPagina - 1) + " não existe, por isso não pode ser cadastrada.");
            return false;
        } else if (linhas.get(i).contains("environment-val")) { // pega ENVIRONMENT
            issue.setAmbiente(pegaAmbiente(linhas, i));
        } else if (linhas.get(i).contains("summary-val")) { // pega NAME
            issue.setNome(pegaNome(linhas.get(i + 1)));
        } else if (linhas.get(i).contains("type-val")) { // pega TIPO
            issue.setTipo(pegaTipo(linhas.get(i + 1)));
        } else if (linhas.get(i).contains("versions-val")) { // pega VERSAO AFETADA
            issue.setVersoesAfetadas(pegaVersoes(linhas, i));
        } else if (linhas.get(i).contains("status-val")) { // pega STATUS
            issue.setStatus(pegaStatus(linhas.get(i + 1)));
        } else if (linhas.get(i).contains("resolution-val")) { // pega RESOLUCAO
            issue.setResolucao(pegaResolucao(linhas.get(i + 1)));
        } else if (linhas.get(i).contains("fixfor-val")) { // pega VERSAO FIXADA
            issue.setVersoesFixadas(pegaVersoes(linhas, i));
        } else if (linhas.get(i).contains("assignee-val")) { // pega ASSIGNEE
            issue.setAssignee(pegaLogin(linhas.get(i)));
        } else if (linhas.get(i).contains("reporter-val")) { // pega REPORTER
            issue.setReporter(pegaLogin(linhas.get(i)));
        } else if (linhas.get(i).contains("priority-val")) { // pega PRIORIDADE
            issue.setPrioridade(pegaPrioridade(linhas.get(i + 1)));
        } else if (linhas.get(i).contains("wrap-labels")) { // pega LABELS
            pegaLabels(issue, linhas, i + 3);
        } else if (linhas.get(i).contains("Target Version/s")) { // pega TARGET VERSIONS
            pegaTargeVersions(issue, linhas.get(i + 3));
        } else if (linhas.get(i).contains("description-val")) { // pega DESCRICAO
            issue.setDescricao(pegaDescricao(linhas, i));
        } else if (linhas.get(i).contains("components-val")) { // pega COMPONENTES
            issue.setComponentes(pegaComponentes(linhas, i));
        } else if (linhas.get(i).contains("create-date")) { // pega DATA CRIADA
            issue.setDataCriada(pegaData(linhas.get(i - 2)));
        } else if (linhas.get(i).contains("resolved-date")) { // pega DATA RESOLVIDA
            issue.setDataResolvida(pegaData(linhas.get(i - 2)));
        }
        return true;
    }

    private void pegaTargeVersions(Issue issue, String linha) {
        try {
            String[] versions = linha.split("</a>");

            for (int i = 0; i < versions.length - 1; i++) {
                String[] partes = versions[i].split(">");
                issue.addTargetVersion(new TargetVersion(partes[1]));
            }
            System.out.println("----------- Capturado Target Versions da Issue ------------");
            System.out.println("Targets: " + issue.getTargetVersions().toString());
            System.out.println("-----------------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("-------- Erro ao capturar Target Versions da Issue --------");
            ex.printStackTrace();
            System.err.println("-----------------------------------------------------------");
        }

    }

    private void pegaLabels(Issue issue, List<String> linhas, int i) {
        try {
            if (linhas.get(i).contains("None")) {
                return;
            }
            i = i + 1;
            while (!linhas.get(i).contains("</div>")) {
                if (linhas.get(i).contains("<span>")) {
                    issue.addLabel(pegaLabel(linhas.get(i)));
                }
                i++;
            }
            System.out.println("----------- Capturado Labels da Issue ------------");
            System.out.println("Labels: " + issue.getLabels() == null || issue.getLabels().isEmpty()
                    ? "None" : issue.getLabels().toString());
            System.out.println("-----------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("-------- Erro ao capturar Labels da Issue --------");
            System.err.println(linhas.get(i));
            ex.printStackTrace();
            System.err.println("-----------------------------------------------------");
        }
    }

    private Label pegaLabel(String linha) {
        //<li><a class="lozenge" href="/jira/secure/IssueNavigator.jspa?reset=true&jqlQuery=labels+%3D+job-filter" title="job-filter"><span>job-filter</span></a></li>
        Label label = new Label();
        String[] partes = linha.split("</span>");
        partes = partes[0].split("<span>");
        label.setLabel(partes[1]);
        return label;
    }

    private Date pegaData(String linha) {
//<dd id="create-date" class="date user-tz"  title="18/Nov/08 10:31" >
// added a comment  - <span class='commentdate_12648727_verbose subText'><span class='date user-tz' title='18/Nov/08 19:52'><time datetime='2008-11-18T19:52+0000'>18/Nov/08 19:52</time></span></span>  </div>
        Date data = null;
        try {
            String[] partes = linha.split("title=");
            partes = partes[1].split(">");
            partes[0] = partes[0].replaceAll("\"", "").replaceAll("'", "".trim());
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
            partes = partes[0].split("/");
            for (int i = 0; i < meses[0].length; i++) {
                if (partes[1].equals(meses[0][i])) {
                    partes[1] = meses[1][i];
                }
            }

            data = df.parse(partes[0] + "/" + partes[1] + "/" + partes[2]);

            System.out.println("----------- Capturado Data da Issue ------------");
            System.out.println("Data: " + data.toString());
            System.out.println("String: " + partes[0] + "/" + partes[1] + "/" + partes[2]);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("-------- Erro ao capturar Data da Issue --------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return data;
    }

    private String pegaComponentes(List<String> linhas, int i) {
//    <span id="components-val" class="value">
//        <span class="shorten" id="components-field">
//              <a href="/jira/browse/LUCENE/component/12311546" title="general/build issues with building Lucene using the ANT build scripts">general/build</a> 
        String componentes = "";
        try {
            if (linhas.get(i + 1).contains("None")) {
                componentes = "None";
            } else {
                componentes = Util.removeCodigoHTML(linhas.get(i + 2), false).trim();
            }
            System.out.println("-------- Capturado Componente da Issue ---------");
            System.out.println("Componente: " + componentes);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("----- Erro ao capturar Componente da Issue -----");
            System.err.println(linhas.get(i + 2));
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return componentes;
    }

    private String pegaComponente(String comp) throws Exception {
//<a href="/jira/browse/LUCENE/component/12311546" title="general/build issues with building Lucene using the ANT build scripts">general/build</a>                             
        String nome = "";
        String link = "";
        String[] partes = comp.split(">");
        partes = partes[1].split("<");
        nome = partes[0];
        partes = comp.split("\"");
        link = partes[1];
        return "nome=" + nome + " / link=" + link;
    }

    private String pegaPrioridade(String linha) {
        String prioridade = null;
        try {
            prioridade = Util.removeCodigoHTML(linha, false).trim();
            System.out.println("-------- Capturado Prioridade da Issue ---------");
            System.out.println("Prioridade: " + prioridade);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("----- Erro ao capturar Prioridade da Issue -----");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return prioridade;
    }

    private String pegaLogin(String linha) {
//        <a class="user-hover" rel="jdillon" id="issue_summary_assignee_jdillon" href="/jira/secure/ViewProfile.jspa?name=jdillon">Jason Dillon</a>
        String login = "";
        try {
            login = Util.removeCodigoHTML(linha, false).trim();
            System.out.println("--------- Capturado Assignee da Issue ----------");
            System.out.println("Assignee: " + login);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Assignee da Issue ------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return login;
    }

    private String pegaResolucao(String linha) {
        String resol = null;
        try {
            resol = linha.trim();
            System.out.println("--------- Capturado Resolucao da Issue ---------");
            System.out.println("Resolucao: " + resol);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Resolucao da Issue -----");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return resol;
    }

    private String pegaStatus(String linha) {
        String status = null;
        try {
            status = Util.removeCodigoHTML(linha, false).trim();
            System.out.println("---------- Capturado Status da Issue -----------");
            System.out.println("Status: " + status);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Status da Issue -------");
            System.err.println("");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return status;
    }

    private String pegaVersoes(List<String> linhas, int i) {
        //    <span title="2.4 ">2.4</span>,                                                            <span title="2.9 Last release (barring major bugs) before migrating to 3.0 and JDK 1.5">2.9</span>                                                    </span>
        String versoes = "";
        try {
            if (linhas.get(i + 1).contains("None")) {
                versoes = "None";
            } else {
                String linha = "";
                int j = i + 2;
                while (!linhas.get(j).contains("</div>")) {
                    linha += linhas.get(j);
                    j++;
                }
                if (linha.contains(">,")) {
                    String[] comps = linha.split(">,");
                    for (String comp : comps) {
                        versoes += pegaVersao(comp) + ";";
                    }
                } else {
                    versoes = pegaVersao(linha);
                }
            }
            System.out.println("---------- Capturado Versoes da Issue ----------");
            System.out.println("Versoes: " + versoes);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Versoes da Issue -------");
            System.err.println(linhas.get(i));
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return versoes;
    }

    private String pegaVersao(String ver) {
//<a href="/jira/browse/LUCENE/component/12311546" title="general/build issues with building Lucene using the ANT build scripts">general/build</a>                             
        String versao = "";
        String[] partes = ver.split(">");
        partes = partes[1].split("<");
        versao = partes[0];
        return "versao=" + versao;
    }

    private String pegaTipo(String linha) {
//                    <span id="type-val" class="value">
//                                                        <img alt="Bug" height="16" src="/jira/images/icons/bug.gif" title="Bug - A problem which impairs or prevents the functions of the product." width="16" />
//   *          Bug
//            </span>
        String tipo = "";
        try {
            tipo = Util.removeCodigoHTML(linha, false).trim();
            System.out.println("----------- Capturado Tipo da Issue ------------");
            System.out.println("Tipo: " + tipo);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Tipo da Issue ---------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return tipo;
    }

    private String pegaNome(String linha) {
        // <h2 id="issue_header_summary" class="item-summary"><a href="/jira/browse/LUCENE-1400">Add Apache RAT (Release Audit Tool) target to build.xml</a></h2>

//<h1 id="summary-val">
//    QueryParser does not recognized negative numbers...
//    
//</h1>

        String nome = "";
        try {
            nome = linha.trim();
            System.out.println("----------- Capturado Nome da Issue ------------");
            System.out.println("Nome: " + nome);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Nome da Issue ---------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return nome;
    }

    private String pegaAmbiente(List<String> linhas, int i) {
//          <div id="environment-val" class="value">
//             <p>jdk 1.5.0_13<br/>
//               ant 1.7.1<br/>
//               osx 10.5.5 </p>
        String ambiente = "";
        i++;
        try {
            while (!linhas.get(i).contains("</div>")) {
                String linha = linhas.get(i).trim();
                linha = linha.replaceAll("<p>", "");
                linha = linha.replaceAll("</p>", "");
                linha = linha.replaceAll("<br/>", "\n");
                ambiente += linha;
                i++;
            }
            System.out.println("--------- Capturado Ambiente da Issue ---------");
            System.out.println("Ambiente: " + ambiente);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("----- Erro ao capturar Ambiente da Issue -------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return ambiente;
    }

    private void lerComentarios(Issue issue, List<String> linhas, boolean novaIssue) {
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).contains("action-body flooded")) {
                Comentario comentario = pegarComentario(linhas, i);
                if (novaIssue || dataInicial == null || comentario.getDataComentario() == null || dataInicial.before(comentario.getDataComentario())) { // verifica se a data do cometario é maior que a dataInicial
                    if (comentario != null && Connection.dao.insere(comentario)) {
                        issue.addComentario(comentario);
                        if (Connection.dao.atualiza(issue)) {
                            System.err.println("\n--------- Comentário Cadastrado e adicioado a Issue ---------");
                            System.err.println("Autor: " + comentario.getAutor());
                            System.err.println("Data: " + comentario.getDataComentario());
                            System.err.println("Comentario: " + comentario.getComentario());
                            System.err.println("-----------------------------------------------------------------\n");
                        } else {
                            System.err.println("\n--------- Comentário Cadastrado e *NÃO* adicioado a Issue ---------");
                            System.err.println("Autor: " + comentario.getAutor());
                            System.err.println("Data: " + comentario.getDataComentario());
                            System.err.println("Comentario: " + comentario.getComentario());
                            System.err.println("-----------------------------------------------------------------\n");
                            Util.writeToFile(logFile, "\t- Comentário [" + comentario.getAutor() + " | " + comentario.getDataComentario() + "] cadastrado e *NÃO* adicioado a Issue.");
                        }
                    } else {
                        System.err.println("\n-------- Erro ao Cadastrar Comentario ----------");
                        System.err.println("Autor: " + comentario.getAutor());
                        System.err.println("Data: " + comentario.getDataComentario());
                        System.err.println("Comentario: " + comentario.getComentario());
                        System.err.println("-----------------------------------------------------------------\n");
                        Util.writeToFile(logFile, "\t- Comentário [" + comentario.getAutor() + " | " + comentario.getDataComentario() + "]  não cadastrado.");
                    }
                } else {
                    System.err.println("\n-------- Comentario fora da data informada ----------");
                    System.err.println("Autor: " + comentario.getAutor());
                    System.err.println("Data: " + comentario.getDataComentario());
                    System.err.println("Comentario: " + comentario.getComentario());
                    System.err.println("-----------------------------------------------------------------\n");
                }
                comentario = null;
            }
        }
        issue = null;
        linhas = null;
    }

    private Comentario pegarComentario(List<String> linhas, int i) {
        Comentario comentario = new Comentario();
        comentario.setAutor(pegaLogin(linhas.get(i - 2)));
        comentario.setDataComentario(pegaData(linhas.get(i - 1)));
        String linha = linhas.get(i);
        String coment = "";
        while (!linha.contains("twixi-wrap concise actionContainer")) {
            if (!linha.trim().isEmpty()) {
                coment += linha.replaceAll("<div class=\"action-body flooded\">", "") + "\n";
            }
            i++;
            linha = linhas.get(i);
        }
        try {
            if (filtrarStrings) {
                coment = Util.filterChar(coment);
                coment = Util.filterChar(Util.removeCodigoHTML(coment, false));
            }
            if (codificarStrings) {
                coment = URLEncoder.encode(coment, "UTF-8");
            }
            comentario.setComentario(coment);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return comentario;
    }

    private String pegaHora(String linha) {
        // added a comment  - <span class='commentdate_12648727_verbose subText'><span class='date user-tz' title='18/Nov/08 19:52'><time datetime='2008-11-18T19:52+0000'>18/Nov/08 19:52</time></span></span>  </div>
        String hora = "";
        try {
            String[] partes = linha.split("</time>");
            partes = partes[0].split(" ");
            hora = partes[partes.length - 1].trim();
            System.out.println("--------- Capturado Hora do Comentario ---------");
            System.out.println("Hora: " + hora);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("----- Erro ao capturar Hora do Comentario ------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return hora;
    }

    private void lerCommits(Issue issue, List<String> linhas, boolean novaIssue) {
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).contains("<td bgcolor=\"#f0f0f0\" width=\"10%\"><b>User</b></td>")) {
                Commits commit = pegarCommit(linhas, i);
                if (novaIssue || dataInicial == null || commit.getDataHora() == null || dataInicial.before(commit.getDataHora())) { // verifica se a data do commit é maior que a dataInicial
                    if (commit != null && Connection.dao.insere(commit)) {
                        leiArquivosModificados(commit, linhas, i + 13);
                        issue.addCommit(commit);
                        if (Connection.dao.atualiza(issue)) {
                            System.err.println("\n--------- Commit Cadastrado e adicioado a Issue ---------");
                            System.err.println("Autor: " + commit.getAutor());
                            System.err.println("Data: " + commit.getDataHora());
                            System.err.println("Mesangem: " + commit.getMensagem());
                            System.err.println("-----------------------------------------------------------------\n");
                        } else {
                            System.err.println("\n--------- Commit Cadastrado e *NÃO* adicioado a Issue ---------");
                            System.err.println("Autor: " + commit.getAutor());
                            System.err.println("Data: " + commit.getDataHora());
                            System.err.println("Mesangem: " + commit.getMensagem());
                            System.err.println("-----------------------------------------------------------------\n");
                            Util.writeToFile(logFile, "\t- Commit [" + commit.getAutor() + " | " + commit.getnRevisao() + " | " + commit.getDataHora() + "] cadastrado e *NÃO* adicioado a Issue.");
                        }
                    } else {
                        System.err.println("\n-------- Erro ao Cadastrar Commit ----------");
                        System.err.println("Autor: " + commit.getAutor());
                        System.err.println("Data: " + commit.getDataHora());
                        System.err.println("Mesangem: " + commit.getMensagem());
                        System.err.println("-----------------------------------------------------------------\n");
                        Util.writeToFile(logFile, "\t- Commit [" + commit.getAutor() + " | " + commit.getnRevisao() + " | " + commit.getDataHora() + "] não cadastrado.");
                    }
                } else {
                    System.err.println("\n-------- Commit fora da data informada ----------");
                    System.err.println("Autor: " + commit.getAutor());
                    System.err.println("Data: " + commit.getDataHora());
                    System.err.println("Mesangem: " + commit.getMensagem());
                    System.err.println("-----------------------------------------------------------------\n");
                }
                commit = null;
            }
        }
        issue = null;
        linhas = null;
    }

    private Commits pegarCommit(List<String> linhas, int i) {
        Commits commit = new Commits();
        commit.setRepositorio(pegaRespositorio(linhas.get(i + 4)));
        commit.setnRevisao(pegaRevisao(linhas.get(i + 5)));
        commit.setDataHora(pegaDataHoraCommit(linhas.get(i + 6)));
        commit.setAutor(pegaLoginCommit(linhas.get(i + 7)));
        commit.setMensagem(pegaMensagemCommit(linhas, i + 8)); // pega mensagem com tags html por nao seguir um padrao
        return commit;
    }

    private String pegaMensagemCommit(List<String> linhas, int i) {
//       <td bgcolor="#ffffff">    Add missing license for <a href="/jira/browse/IVY-1" title="use MBeans do find projects"><strike>IVY-1</strike></a>.4.1<br/>
//   Issue: <a href="/jira/browse/FOR-855" title="verify the license situation prior to each release">FOR-855</a>
//   </td>
        String mensagem = "";
        String linha = "";
        try {
            linha = linhas.get(i);
            while (!linha.contains("</tr>")) {
                mensagem += linha;
                i++;
                linha = linhas.get(i);
            }
            System.out.println("--------- Capturado Mensagem do Commit ---------");
            System.out.println("Mensagem: " + mensagem);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Mensagem do Commit -----");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return mensagem;
    }

    private void leiArquivosModificados(Commits commit, List<String> linhas, int i) {
        while (!linhas.get(i).contains("<td bgcolor=\"#f0f0f0\" width=\"10%\"><b>Repository</b></td>")
                && !linhas.get(i).contains("</table>")) {
            if (linhas.get(i).contains("<font ") && linhas.get(i).contains("<b ")) {
                ArquivoModificado arquivo = new ArquivoModificado();
                arquivo.setAcao(pegaAcaoArquivoModificado(linhas.get(i)));
                arquivo.setUrl(pegaURLArquivoModificado(linhas.get(i + 1)));
                arquivo.setNome(pegaNomeArquivoModificado(linhas.get(i + 1)));
                if (Connection.dao.insere(arquivo)) {
                    commit.addArquivoModificado(arquivo);
                }
                arquivo = null;
            }
            i++;
        }
    }

    private String pegaAcaoArquivoModificado(String linha) {
//                                    <font color="#999933" size="-2"><b title="Modify">MODIFY</b></font>
        String acao = "";
        try {
            String[] partes = linha.split("</b>");
            partes = partes[0].split(">");
            acao = partes[partes.length - 1];
            System.out.println("---------- Capturado Acao do Arquivo -----------");
            System.out.println("Acao: " + acao);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Acao do Arquivo -------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return acao;
    }

    private String pegaURLArquivoModificado(String linha) {
//                        <a href="http://svn.apache.org/viewvc/lucene/java/branches/flex_1458/src/java/org/apache/lucene/index/FreqProxFieldMergeState.java/?rev=885265&view=diff&r1=885265&r2=885264&p1=/lucene/java/branches/flex_1458/src/java/org/apache/lucene/index/FreqProxFieldMergeState.java&p2=/lucene/java/branches/flex_1458/src/java/org/apache/lucene/index/FreqProxFieldMergeState.java">/lucene/java/branches/flex_1458/src/java/org/apache/lucene/index/FreqProxFieldMergeState.java</a>
        String url = "";
        try {
            String[] partes = linha.split("href=\"");
            partes = partes[1].split("\"");
            url = partes[0];
            System.out.println("----------- Capturado URL do Arquivo -----------");
            System.out.println("URL: " + url);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar URL do Arquivo --------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return url;
    }

    private String pegaNomeArquivoModificado(String linha) {
        String nome = "";
        try {
            nome = pegaSomenteConteudo(linha);
            System.out.println("---------- Capturado Nome do Arquivo -----------");
            System.out.println("URL: " + nome);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Nome do Arquivo -------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return nome;
    }

    private Date pegaDataHoraCommit(String linha) {
//     <td bgcolor="#ffffff" width="10%" valign="top" rowspan="3">Wed Sep 03 21:58:13 UTC 2008</td>
        Date data = null;
        try {
            linha = pegaSomenteConteudo(linha);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String[][] meses = new String[][]{{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}, {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"}};
            String[] partes = linha.split(" ");
            for (int i = 0; i < meses[0].length; i++) {
                if (partes[1].equals(meses[0][i])) {
                    partes[1] = meses[1][i];
                }
            }
            String dt = partes[2] + "/" + partes[1] + "/" + partes[5] + " " + partes[3];
            data = df.parse(dt);
            System.out.println("--------- Capturado Revisao do Commit ----------");
            System.out.println("Revisao: " + data);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Revisao do Commit ------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return data;
    }

    private String pegaSomenteConteudo(String linha) throws Exception {
        String[] partes = linha.split("<");
        partes = partes[partes.length - 2].split(">");
        return partes[partes.length - 1];
    }

    private String pegaRevisao(String linha) {
//     <td bgcolor="#ffffff" width="10%" valign="top" rowspan="3"><a href="http://svn.apache.org/viewvc?view=rev&rev=691802">#691802</a></td>
        String revisao = "";
        try {
            String link = "";
            String num = "";
            String[] partes = linha.split("</a>");
            partes = partes[0].split(">");
            num = partes[partes.length - 1];
            partes = partes[partes.length - 2].split("\"");
            link = partes[partes.length - 1];
            revisao = "numero=" + num + " / link=" + link;
            System.out.println("--------- Capturado Revisao do Commit ----------");
            System.out.println("Revisao: " + revisao);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------ Erro ao capturar Revisao do Commit ------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return revisao;
    }

    private String pegaRespositorio(String linha) {
//    <td bgcolor="#ffffff" width="10%" valign="top" rowspan="3">ASF</td>        
        String repositorio = "";
        try {
            repositorio = pegaSomenteConteudo(linha);
            System.out.println("------- Capturado Respositorio do Commit -------");
            System.out.println("Login: " + repositorio);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("--- Erro ao capturar Respositorio do Commit ----");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return repositorio;
    }

    private String pegaLoginCommit(String linha) {
//     <td bgcolor="#ffffff" width="10%" valign="top" rowspan="3">maartenc</td>
        String login = "";
        try {
            login = pegaSomenteConteudo(linha);
            System.out.println("---------- Capturado Login do Commit -----------");
            System.out.println("Login: " + login);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("------- Erro ao capturar Login do Commit -------");
            System.err.println(linha);
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return login;
    }

    private String getUrl() {
        return projeto.getLinkIssue().replaceAll("-1", "-" + numeroProximaPagina);
    }

    /**
     * Passe 'true' ou 'false' para que as Strings sejam ou não codificadas em
     * UTF-8 e filtradas antes de gravar no banco de dados. Se nada for passado,
     * será assumido como 'true'.
     *
     * @param codificarStrings true ou false.
     * @param filtrarStrings true ou false.
     */
    public void setFiltrarCodificarStrings(boolean filtrarStrings, boolean codificarStrings) {
        this.filtrarStrings = filtrarStrings;
        this.codificarStrings = codificarStrings;
    }
}
