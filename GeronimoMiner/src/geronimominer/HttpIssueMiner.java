/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import pojo.Comentario;
import pojo.Issue;
import pojo.Projeto;
import util.Util;

/**
 *
 * @author Douglas
 */
public class HttpIssueMiner {

    private int numeroProximaPagina;
    private Projeto projeto;
    private String logFile;
    private int inexistentes;

    public HttpIssueMiner() {
    }

    public HttpIssueMiner(Projeto projeto, int numeroProximaPagina) {
        this.projeto = projeto;
        this.numeroProximaPagina = numeroProximaPagina;
        this.logFile = "src/" + projeto.getxKey();
        inexistentes = 0;
    }

    public void minerarIssues() throws Exception {

        System.out.println("");
        System.out.println("-----------------------------------------");
        System.out.println("Iniciando a mineração das Issues");
        System.out.println("-----------------------------------------\n");
        writeToFile(logFile, "Início da mineração: " + new Date() + "\n");

        while (inexistentes <= 40) {
            System.out.println("---- Conectando a URL : " + getUrl());
            URL url = new URL(getUrl());
            BufferedReader dis = new BufferedReader(new InputStreamReader(Util.abrirStream(url)));
            System.out.println("---- Conectado a URL : " + getUrl());
            lerPaginaHtml(capturarCodigoHtml(dis));
            dis.close();
            url = null;
            dis = null;
        }

        writeToFile(logFile, "Fim da mineração: " + new Date() + "\n");
        System.out.println("-----------------------------------------");
        System.out.println("Terminado a mineração das Issues");
        System.out.println("-----------------------------------------\n");
        
        projeto = null;
    }

    private void lerPaginaHtml(String[] linhas) {
        Issue issue = lerIssue(linhas);
        if (issue != null) {
            lerComentarios(issue, linhas);
        }
        issue = null;
        linhas = null;
        numeroProximaPagina++;
    }

    private Issue lerIssue(String[] linhas) {
        Issue issue = new Issue();
        issue.setNumeroIssue(numeroProximaPagina);
        for (int i = 0; i < linhas.length; i++) {
            if (!pegarDadosIssue(issue, linhas, i)) {
                return null;
            }
        }
        if (issue.getNome() != null) {
            inexistentes = 0;
            boolean inseriu = false;
            projeto.addIssue(issue);
            if (AllProjectsMiner.daoProjeto.insere(issue)) {
                if (AllProjectsMiner.daoProjeto.atualiza(projeto)) {
                    System.err.println("----------------------------------------");
                    System.err.println(issue.getNumeroIssue() + ": Issue cadastrado e adicionado ao Projeto");
                    System.err.println("----------------------------------------\n");
                    writeToFile(logFile, "- Issue " + (numeroProximaPagina - 1) + " cadastrada.");
                } else {
                    projeto.removeIssue(issue);
                    System.err.println("----------------------------------------");
                    System.err.println(issue.getNumeroIssue() + ": Issue cadastrado e *NÃO* adicionado ao Projeto");
                    System.err.println("----------------------------------------\n");
                }
                return issue;
            }
            if (!inseriu) {
                System.err.println("----------------------------------------");
                System.err.println(issue.getNumeroIssue() + ": A Issue não pode ser cadastrado. Algo não está certo...");
                System.err.println("----------------------------------------\n");
                writeToFile(logFile, "- Erro: Issue " + (numeroProximaPagina - 1) + " não foi cadastrada.");
            }
        }
        return null;
    }

    private boolean pegarDadosIssue(Issue issue, String[] linhas, int i) {
        if (linhas[i].contains("<title>Issue Does Not Exist - ASF JIRA </title>")) {
            inexistentes++;
            System.err.println("---------------------------------------------\n");
            System.err.println("A página de Issue não existe");
            System.err.println("---------------------------------------------\n");
            writeToFile(logFile, "- A Issue " + (numeroProximaPagina - 1) + " não existe, por isso não pode ser cadastrada.");
            return false;
        } else if (linhas[i].contains("environment-val")) { // pega ENVIRONMENT
            issue.setAmbiente(pegaAmbiente(linhas, i));
        } else if (linhas[i].contains("issue_header_summary")) { // pega NAME
            issue.setNome(pegaNome(linhas[i]));
        } else if (linhas[i].contains("type-val")
                && linhas[i].contains("class=\"value\"")
                && linhas[i].contains("<span")) { // pega TIPO
            issue.setTipo(pegaTipo(linhas[i + 2]));
        } else if (linhas[i].contains("versions-val")) { // pega VERSAO AFETADA
            issue.setVersoesAfetadas(pegaVersoes(linhas, i));
        } else if (linhas[i].contains("status-val")) { // pega STATUS
            issue.setStatus(pegaStatus(linhas[i + 2]));
        } else if (linhas[i].contains("resolution-val")) { // pega RESOLUCAO
            issue.setResolucao(pegaResolucao(linhas[i + 1]));
        } else if (linhas[i].contains("fixfor-val")) { // pega VERSAO FIXADA
            issue.setVersoesFixadas(pegaVersoes(linhas, i));
        } else if (linhas[i].contains("assignee-val")) { // pega ASSIGNEE
            issue.setAssignee(pegaLogin(linhas, i));
        } else if (linhas[i].contains("reporter-val")) { // pega REPORTER
            issue.setReporter(pegaLogin(linhas, i));
        } else if (linhas[i].contains("priority-val")) { // pega PRIORIDADE
            issue.setPrioridade(pegaPrioridade(linhas[i + 2]));
        } else if (linhas[i].contains("components-val")) { // pega COMPONENTES
            issue.setComponentes(pegaComponentes(linhas, i));
        } else if (linhas[i].contains("create-date")) { // pega DATA CRIADA
            issue.setDataCriada(pegaData(linhas[i + 1]));
        } else if (linhas[i].contains("resolved-date")) { // pega DATA RESOLVIDA
            issue.setDataResolvida(pegaData(linhas[i + 1]));
        }

        return true;
    }

    private Date pegaData(String linha) {
//                        <time datetime="2008-11-18T10:31+0000">18/Nov/08 10:31</time></dd>                     </dd>
// added a comment  - <span class='commentdate_12648727_verbose subText'><span class='date user-tz' title='18/Nov/08 19:52'><time datetime='2008-11-18T19:52+0000'>18/Nov/08 19:52</time></span></span>  </div>
        Date data = null;
        try {
            String[] partes = linha.split("datetime=");
            partes = partes[1].split("T");
            data = Util.stringToDate(partes[0].replaceAll("\"", "").replaceAll("'", ""));
            System.out.println("----------- Capturado Data da Issue ------------");
            System.out.println("Componente: " + data.toString());
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("-------- Erro ao capturar Data da Issue --------");
            ex.printStackTrace();
            System.err.println("------------------------------------------------");
        }
        return data;
    }

    private String pegaComponentes(String[] linhas, int i) {
//    <span id="components-val" class="value">
//        <span class="shorten" id="components-field">
//              <a href="/jira/browse/LUCENE/component/12311546" title="general/build issues with building Lucene using the ANT build scripts">general/build</a> 
        String nomeComp = "";
        String urlComp = "";
        String componentes = "";
        try {
            if (linhas[i + 1].contains("None")) {
                componentes = "None";
            } else {
                String linha = "";
                int j = i + 2;
                while (!linhas[j].contains("</div>")) {
                    linha += linhas[j];
                    j++;
                }
                if (linha.contains(">,")) {
                    String[] comps = linha.split(">,");
                    for (String comp : comps) {
                        componentes += pegaComponente(comp) + ";";
                    }
                } else {
                    componentes = pegaComponente(linha);
                }
            }
            System.out.println("-------- Capturado Componente da Issue ---------");
            System.out.println("Componente: " + componentes);
            System.out.println("------------------------------------------------");
        } catch (Exception ex) {
            System.err.println("----- Erro ao capturar Componente da Issue -----");
            System.err.println(linhas[i + 2]);
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
            prioridade = linha.trim();
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

    private String pegaLogin(String[] linhas, int i) {
//        <a class="user-hover" rel="jdillon" id="issue_summary_assignee_jdillon" href="/jira/secure/ViewProfile.jspa?name=jdillon">Jason Dillon</a>
        String login = "";
        try {
            if (!linhas[i + 1].contains("<a")) {
                login = "unassigned";
            } else {
                String[] partes = linhas[i + 1].split("rel=\"");
                partes = partes[1].split("\"");
                login = partes[0];
            }
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
            resol = linha.trim().replaceAll("&#39;", "'");
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
            status = linha.trim();
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

    private String pegaVersoes(String[] linhas, int i) {
        //    <span title="2.4 ">2.4</span>,                                                            <span title="2.9 Last release (barring major bugs) before migrating to 3.0 and JDK 1.5">2.9</span>                                                    </span>
        String versoes = "";
        try {
            if (linhas[i + 1].contains("None")) {
                versoes = "None";
            } else {
                String linha = "";
                int j = i + 2;
                while (!linhas[j].contains("</div>")) {
                    linha += linhas[j];
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
            System.err.println(linhas[i]);
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
            tipo = linha.trim();
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
        String nome = "";
        try {
            String[] partes = linha.split("</a>");
            partes = partes[0].split(">");
            nome = partes[partes.length - 1];
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

    private String pegaAmbiente(String[] linhas, int i) {
//          <div id="environment-val" class="value">
//             <p>jdk 1.5.0_13<br/>
//               ant 1.7.1<br/>
//               osx 10.5.5 </p>
        String ambiente = "";
        i++;
        try {
            while (!linhas[i].contains("</div>")) {
                String linha = linhas[i].trim();
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

    private void lerComentarios(Issue issue, String[] linhas) {
        for (int i = 0; i < linhas.length; i++) {
            if (linhas[i].contains("action-body flooded")) {
                Comentario comentario = pegarComentario(linhas, i);
                if (comentario != null && AllProjectsMiner.daoProjeto.insere(comentario)) {
                    issue.addComentario(comentario);
                    if (AllProjectsMiner.daoProjeto.atualiza(issue)) {
                        System.err.println("\n--------- Comentário Cadastrado e adicioado a Issue ---------");
                        System.err.println("Autor: " + comentario.getAutor());
                        System.err.println("Data: " + comentario.getDataComentario() + " / " + comentario.getHoraComentario());
                        System.err.println("Comentario: " + comentario.getComentario());
                        System.err.println("-----------------------------------------------------------------\n");
                    } else {
                        System.err.println("\n--------- Comentário Cadastrado e *NÃO* adicioado a Issue ---------");
                        System.err.println("Autor: " + comentario.getAutor());
                        System.err.println("Data: " + comentario.getDataComentario() + " / " + comentario.getHoraComentario());
                        System.err.println("Comentario: " + comentario.getComentario());
                        System.err.println("-----------------------------------------------------------------\n");
                    }
                } else {
                    System.err.println("\n-------- Erro ao Cadastrar Comentario ----------");
                    System.err.println("");
                    System.err.println("------------------------------------------------\n");
                }
                comentario = null;
            }
        }
        issue = null;
        linhas = null;
    }

    private Comentario pegarComentario(String[] linhas, int i) {
        Comentario comentario = new Comentario();
        comentario.setAutor(pegaLogin(linhas, i - 3));
        comentario.setDataComentario(pegaData(linhas[i - 1]));
        comentario.setHoraComentario(pegaHora(linhas[i - 1]));
        String linha = linhas[i];
        String coment = "";
        while (!linha.contains("twixi-wrap concise actionContainer")) {
            if (!linha.trim().isEmpty()) {
                coment += linha.replaceAll("<div class=\"action-body flooded\">", "") + "\n";
            }
            linha = linhas[i++];
        }
        try {
            comentario.setComentario(URLEncoder.encode(coment, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
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

    private String getUrl() {
        return projeto.getLinkIssue().replaceAll("-1", "-" + numeroProximaPagina);
    }

    private boolean writeToFile(String caminho, String msg) {
        File f = new File(caminho);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(msg);
            pw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private String[] capturarCodigoHtml(BufferedReader dis) throws Exception {
        StringBuilder sb = new StringBuilder();
        String linha = dis.readLine();
        while (!linha.trim().equals("</html>")) {
            sb.append(linha);
            sb.append("\n");
            linha = dis.readLine();
        }
        sb.append(linha);
        dis = null;
        return sb.toString().split("\n");
    }
}
