package modelo;

/**
 *
 * @author Victor
 */
public class Productos {
    private int id, cantidad;
    private String nombre;
    private boolean estatus;

    public Productos(int id,String nombre, int cantidad,  boolean estatus) {
        this.id = id;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.estatus = estatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }


}
