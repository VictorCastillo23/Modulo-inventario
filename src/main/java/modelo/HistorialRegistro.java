package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author Victor
 */
public class HistorialRegistro {
    private int idHistorico;
    private String nombreUsuario;
    private String nombreProducto;
    private String movimiento;
    private int cantidad;
    private LocalDateTime fecha;

    public HistorialRegistro(int idHistorico, String nombreUsuario, String nombreProducto,
                             String movimiento, int cantidad, LocalDateTime fecha) {
        this.idHistorico = idHistorico;
        this.nombreUsuario = nombreUsuario;
        this.nombreProducto = nombreProducto;
        this.movimiento = movimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getIdHistorico() { return idHistorico; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreProducto() { return nombreProducto; }
    public String getMovimiento() { return movimiento; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFecha() { return fecha; }

    public String getFechaFormateada() {
        if (fecha == null) return "";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
