package modelo;

/**
 * 
 * @author Victor
 */
public class Usuario {
    private int id,idRol;
    private String usuario;

    public Usuario(int id, String usuario,int idRol) {
        this.id = id;
        this.usuario = usuario;
        this.idRol = idRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}
