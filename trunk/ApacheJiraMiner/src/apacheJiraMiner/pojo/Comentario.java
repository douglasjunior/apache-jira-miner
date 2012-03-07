/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author kojy
 */
@Entity
@Table(name = "COMENTARIO")
public class Comentario implements Serializable {

    //private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataComentario;
    private String autor;
    @Column(columnDefinition = "LONGTEXT")
    private String comentario;
    @ManyToOne
    private Issue issue;

    public Comentario() {
    }

    public Comentario(Issue issue) {
        issue.addComentario(this);
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Date getDataComentario() {
        return dataComentario;
    }

    public void setDataComentario(Date dataComentario) {
        this.dataComentario = dataComentario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Comentario)) {
            return false;
        }
        Comentario other = (Comentario) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (this.getComentario() == null) {
            return null;
        } else {
            return "Data:" + getDataComentario() + " Autor:" + getAutor() + " Comentario:" + getComentario();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean estaCompleto() {
        if (getAutor() == null
                || getComentario() == null
                || getDataComentario() == null) {
            return false;
        }
        return true;
    }
}
