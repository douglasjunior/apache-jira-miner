/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "COMMITS")
public class Commits implements Serializable {

    //private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "LONGTEXT")
    private String mensagem;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHora;
    @Column(length = 100)
    private String autor;
    @Column(length = 100)
    private String repositorio;
    private String nRevisao;
    @OneToMany(mappedBy = "commits", cascade = CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMITS_ID")
    private List<ArquivoModificado> arquivos;
    @ManyToOne
    private Issue issue;

    public Commits() {
        arquivos = new ArrayList<ArquivoModificado>();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ArquivoModificado> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoModificado> arquivos) {
        this.arquivos = arquivos;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getnRevisao() {
        return nRevisao;
    }

    public void setnRevisao(String nRevisao) {
        this.nRevisao = nRevisao;
    }
    
    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }



    public String getRepositorio() {
        return repositorio;
    }

    public void setRepositorio(String repositorio) {
        this.repositorio = repositorio;
    }
    
    public void addArquivoModificado(ArquivoModificado arquivo){
        if(!getArquivos().contains(arquivo) || arquivo.getId()==0){
            getArquivos().add(arquivo);
        }
        arquivo.setCommits(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Commits)) {
            return false;
        }
        Commits other = (Commits) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojo.Commits[ id=" + id + " ]";
    }
}
