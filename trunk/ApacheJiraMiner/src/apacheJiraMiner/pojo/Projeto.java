/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name="PROJETO")
public class Projeto implements Serializable {

   // private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 100)
    private String nome;
    private String linkIssue;
    @Column(length = 30, unique = true)
    private String xKey;
    @Column(length = 30)
    private String projectLead;
    private String url;
    @Column(length = 30)
    private String grupoProjeto;
    @OneToMany(mappedBy = "projeto",cascade= CascadeType.REFRESH, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJETO_ID")
    private List<Issue> issues;

    public Projeto() {
        issues = new ArrayList<Issue>();
    }

    public String getGrupoProjeto() {
        return grupoProjeto;
    }

    public void setGrupoProjeto(String grupoProjeto) {
        this.grupoProjeto = grupoProjeto;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getxKey() {
        return xKey;
    }

    public void setxKey(String xKey) {
        this.xKey = xKey;
    }

    public String getLinkIssue() {
        return linkIssue;
    }

    public void setLinkIssue(String linkIssue) {
        this.linkIssue = linkIssue;
    }

    public String getProjectLead() {
        return projectLead;
    }

    public void setProjectLead(String projectLead) {
        this.projectLead = projectLead;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addIssue(Issue issue) {
        if (!getIssues().contains(issue) || issue.getId() == 0) {
            getIssues().add(issue);
        }
        issue.setProjeto(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Projeto)) {
            return false;
        }
        Projeto other = (Projeto) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pojo.Projeto[ id=" + id + " ]";
    }

    public void removeIssue(Issue issue) {
        if (getIssues().contains(issue)) {
            getIssues().remove(issue);
        }
        issue.setProjeto(null);
    }
}
