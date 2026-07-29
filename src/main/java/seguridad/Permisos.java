package seguridad;

/**
 * Canonical permission name constants used by {@code ProductosController}
 * to gate each inventory action.
 *
 * <p>These values MUST exactly match the permission names seeded in
 * {@code SCRIPTS/inventario_roles.sql} (and the
 * {@code SCRIPTS/migrations/2026-07-28_01_permisos_rename.sql} migration for
 * existing databases). {@code PermisoDAO} reads permission names as raw
 * strings from the database with no case normalization, so any drift here
 * silently locks every role out of the affected action.
 *
 * <p>JSPs read these same names as EL string literals
 * (e.g. {@code ${permisos.ver_inventario}}) with no compile-time link to
 * this class. Keep the following files in sync manually when a name changes
 * here:
 * <ul>
 *   <li>{@code Productos/index.jsp}</li>
 *   <li>{@code Productos/salida.jsp}</li>
 *   <li>{@code Productos/historial.jsp}</li>
 * </ul>
 */
public final class Permisos {

    public static final String VER_INVENTARIO = "ver_inventario";
    public static final String AGREGAR_PRODUCTOS = "agregar_productos";
    public static final String AUMENTAR_INVENTARIO = "aumentar_inventario";
    public static final String BAJA_REACTIVAR_PRODUCTO = "baja_reactivar_producto";
    public static final String VER_SALIDA = "ver_salida";
    public static final String SACAR_INVENTARIO = "sacar_inventario";
    public static final String VER_HISTORICO = "ver_historico";

    private Permisos() {
    }
}
