package modelo;

import java.time.LocalDateTime;

/**
 * 
 * @author Victor
 */
public class Historico {
    private int idHistorico;
    private int idUsuario;
    private int idProducto;
    private String movimiento;
    private int cantidad;
    private LocalDateTime fecha;

    public Historico(int idHistorico, int idUsuario, int idProducto, String movimiento, int cantidad, LocalDateTime fecha) {
        this.idHistorico = idHistorico;
        this.idUsuario = idUsuario;
        this.idProducto = idProducto;
        this.movimiento = movimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public Historico(int idUsuario, int idProducto, String movimiento, int cantidad) {
        this(0, idUsuario, idProducto, movimiento, cantidad, null);
    }

    public int getIdHistorico() { return idHistorico; }
    public void setIdHistorico(int idHistorico) { this.idHistorico = idHistorico; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getMovimiento() { return movimiento; }
    public void setMovimiento(String movimiento) { this.movimiento = movimiento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
