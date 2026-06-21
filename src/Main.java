
import java.util.List;
import service.CategoriaService;
import service.ProductoService;
import service.PedidoService;
import ui.MenuHelper;
import entities.Categoria;
import entities.Producto;
import entities.Usuario;
import entities.Pedido;
import service.UsuarioService;
import enums.Rol;
import enums.FormaPago;
import java.util.ArrayList;
import enums.Estado;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author simob
 */
public class Main {

    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService productoService = new ProductoService();
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final PedidoService pedidoService = new PedidoService();

    public static void main(String[] args) {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n ----- SISTEMA DE PEDIDOS (FOOD STORE) ------");
            System.out.println("1. Categorias");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");

            opcion = MenuHelper.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    menuCategorias();
                    break;
                case 2:
                    menuProductos();
                    break;
                case 3:
                    menuUsuarios();
                    break;
                case 4:
                    menuPedidos();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion fuera de rango. Intente nuevamente.");
            }
        }
    }

    // SUBMENU DE CATEGORIAS (epica 1)
    private static void menuCategorias() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTION DE CATEGORIAS ---");
            System.out.println("1. Listar Categorias");
            System.out.println("2. Crear Categoria");
            System.out.println("3. Editar Categoria");
            System.out.println("4. Eliminar Categoria");
            System.out.println("0. Volver al Menu Principal");

            opcion = MenuHelper.leerEntero("Seleccione: ");

            try {
                switch (opcion) {
                    case 1: // HU-CAT-01: Listar
                        List<Categoria> activas = categoriaService.listarActivas();
                        if (activas.isEmpty()) {
                            System.out.println("No hay categorias cargadas.");
                        } else {
                            System.out.println("\n--- Listado de Categorias ---");
                            for (Categoria cat : activas) {
                                System.out.println(cat); // Usa el toString() simplificado
                            }
                        }
                        break;

                    case 2: // HU-CAT-02: Crear
                        System.out.println("\n--- Nueva Categoria ---");
                        String nombre = MenuHelper.leerTexto("Nombre de la categoria: ");
                        String desc = MenuHelper.leerTexto("Descripcion: ");
                        Categoria nueva = categoriaService.crear(nombre, desc);
                        System.out.println("Categoria creada con exito. ID generado: " + nueva.getId());
                        break;

                    case 3: // HU-CAT-03: Editar
                        System.out.println("\n--- Editar Categoria ---");
                        Long idEditar = MenuHelper.leerLongOpcional("Ingrese el ID de la categoria a editar: ");
                        String nuevoNombre = MenuHelper.leerTextoOpcional("Nuevo Nombre (deje vacio para mantener actual): ");
                        String nuevaDesc = MenuHelper.leerTextoOpcional("Nueva Descripcion (deje vacio para mantener actual): ");

                        categoriaService.editar(idEditar, nuevoNombre, nuevaDesc);
                        System.out.println("Categoria actualizada correctamente.");
                        break;

                    case 4: // HU-CAT-04: Eliminar
                        System.out.println("\n--- Eliminar Categoria /soft delete/ ---");
                        Long idEliminar = MenuHelper.leerLongOpcional("Ingrese el ID de la categoria a eliminar: ");

                        String confirma = MenuHelper.leerTexto("Esta seguro de eliminar? (S/N): ");
                        if (confirma.equalsIgnoreCase("S")) {
                            // Le pasa el productoService para cumplir la regla de integridad
                            categoriaService.eliminar(idEliminar, productoService);
                            System.out.println("Categoria eliminada logicamente con exito.");
                        } else {
                            System.out.println("Operacion cancelada por el usuario.");
                        }
                        break;

                    case 0:
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                // Atrapa cualquier EntidadNoEncontradaException o ValidacionException sin crashear
                System.out.println("\nERROR: " + e.getMessage());
            }
        }
    }

    // SUBMENU DE PRODUCTOS (epica 2)
    private static void menuProductos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTION DE PRODUCTOS ---");
            System.out.println("1. Listar Productos");
            System.out.println("2. Crear Producto");
            System.out.println("3. Editar Producto");
            System.out.println("4. Eliminar Producto");
            System.out.println("0. Volver al Menu Principal");

            opcion = MenuHelper.leerEntero("Seleccione: ");

            try {
                switch (opcion) {
                    case 1: // HU-PROD-01: Listar
                        List<Producto> activos = productoService.listarActivos();
                        if (activos.isEmpty()) {
                            System.out.println("No hay productos cargados en el catalogo.");
                        } else {
                            System.out.println("\n--- Catalogo de Productos ---");
                            for (Producto prod : activos) {
                                System.out.println(prod); // Usa el toString() sobreescrito
                            }
                        }
                        break;

                    case 2: // HU-PROD-02: Crear
                        System.out.println("\n--- Nuevo Producto ---");
                        String nombre = MenuHelper.leerTexto("Nombre del producto: ");
                        String desc = MenuHelper.leerTexto("Descripcion: ");

                        // Validacion de entrada que no pongan letras donde van numeros
                        Double precio = null;
                        while (precio == null) {
                            precio = MenuHelper.leerDoubleOpcional("Precio: $");
                            if (precio == null) {
                                System.out.println("El precio es obligatorio para crear.");
                            }
                        }

                        Integer stock = null;
                        while (stock == null) {
                            int s = MenuHelper.leerEntero("Stock inicial: ");
                            if (s >= 0) {
                                stock = s;
                            }
                        }

                        String imagen = MenuHelper.leerTexto("URL de la imagen: ");

                        String dispInput = MenuHelper.leerTexto("¿Esta disponible para la venta? (S/N): ");
                        boolean disponible = dispInput.equalsIgnoreCase("S");

                        // Para facilitar la prueba, lista las categorias antes de pedir el ID
                        System.out.println("\nCategorias disponibles:");
                        List<Categoria> cats = categoriaService.listarActivas();
                        for (Categoria c : cats) {
                            System.out.println("  ID: " + c.getId() + " - " + c.getNombre());
                        }

                        Long catId = MenuHelper.leerLongOpcional("Ingrese el ID de la categoria a asociar: ");

                        Producto nuevo = productoService.crear(nombre, precio, desc, stock, imagen, disponible, catId);
                        System.out.println("Producto creado con exito. ID generado: " + nuevo.getId());
                        break;

                    case 3: // HU-PROD-03: Editar
                        System.out.println("\n--- Editar Producto ---");
                        Long idEditar = MenuHelper.leerLongOpcional("Ingrese el ID del producto a editar: ");

                        // Carga de datos. Si da Enter, va null y el servicio no lo toca.
                        String nuevoNombre = MenuHelper.leerTextoOpcional("Nuevo Nombre (Enter para mantener): ");
                        String nuevaDesc = MenuHelper.leerTextoOpcional("Nueva Descripcion (Enter para mantener): ");
                        Double nuevoPrecio = MenuHelper.leerDoubleOpcional("Nuevo Precio (Enter para mantener): $");

                        Long nuevoStockLong = MenuHelper.leerLongOpcional("Nuevo Stock (Enter para mantener): ");
                        Integer nuevoStock = (nuevoStockLong != null) ? nuevoStockLong.intValue() : null;

                        String nuevaImagen = MenuHelper.leerTextoOpcional("Nueva Imagen (Enter para mantener): ");

                        String nuevaDispInput = MenuHelper.leerTextoOpcional("Disponible? (S/N - Enter para mantener): ");
                        Boolean nuevaDisp = null;
                        if (nuevaDispInput != null) {
                            nuevaDisp = nuevaDispInput.equalsIgnoreCase("S");
                        }

                        Long nuevaCatId = MenuHelper.leerLongOpcional("Nuevo ID de Categoria (Enter para mantener): ");

                        productoService.editar(idEditar, nuevoNombre, nuevoPrecio, nuevaDesc, nuevoStock, nuevaImagen, nuevaDisp, nuevaCatId);
                        System.out.println("Producto actualizado correctamente.");
                        break;

                    case 4: // HU-PROD-04: Eliminar
                        System.out.println("\n--- Eliminar Producto /soft delete/ ---");
                        Long idEliminar = MenuHelper.leerLongOpcional("Ingrese el ID del producto a eliminar: ");

                        String confirma = MenuHelper.leerTexto("Estas seguro de retirar este producto del catalogo? (S/N): ");
                        if (confirma.equalsIgnoreCase("S")) {
                            productoService.eliminar(idEliminar);
                            System.out.println("Producto dado de baja con exito.");
                        } else {
                            System.out.println("Operacion cancelada.");
                        }
                        break;

                    case 0:
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("\nERROR: " + e.getMessage());
            }
        }
    }

    // SUBMENU DE USUARIOS (epica 3)
    private static void menuUsuarios() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTION DE USUARIOS ---");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Crear Usuario");
            System.out.println("3. Editar Usuario");
            System.out.println("4. Eliminar Usuario");
            System.out.println("0. Volver al Menu Principal");

            opcion = MenuHelper.leerEntero("Seleccione: ");

            try {
                switch (opcion) {
                    case 1: // HU-USR-01: Listar
                        List<Usuario> activos = usuarioService.listarActivos();//utiliza la variable static definida al inicio
                        if (activos.isEmpty()) {
                            System.out.println("No hay usuarios registrados en el sistema.");
                        } else {
                            System.out.println("\n--- Lista de Usuarios Activos ---");
                            for (Usuario usr : activos) {
                                System.out.println(usr); // Usa el toString() de usuario
                            }
                        }
                        break;

                    case 2: // HU-USR-02: Crear
                        System.out.println("\n--- Nuevo Usuario ---");
                        String nombre = MenuHelper.leerTexto("Nombre: ");
                        String apellido = MenuHelper.leerTexto("Apellido: ");
                        String mail = MenuHelper.leerTexto("Email: ");
                        String celular = MenuHelper.leerTexto("Celular: ");
                        String contrasenia = MenuHelper.leerTexto("Contrasena: ");

                        System.out.println("Seleccione el Rol:");
                        System.out.println("  1. ADMIN");
                        System.out.println("  2. USUARIO");
                        int rolOp = MenuHelper.leerEntero("Opcion: ");
                        Rol rol = (rolOp == 1) ? Rol.ADMIN : Rol.USUARIO;

                        Usuario nuevo = usuarioService.crear(nombre, apellido, mail, celular, contrasenia, rol);
                        System.out.println("Usuario creado con exito. ID generado: " + nuevo.getId());
                        break;

                    case 3: // HU-USR-03: Editar
                        System.out.println("\n--- Editar Usuario ---");
                        Long idEditar = MenuHelper.leerLongOpcional("Ingrese el ID del usuario a editar: ");

                        // Si da Enter, va null y el servicio conserva el valor anterior
                        String nuevoNombre = MenuHelper.leerTextoOpcional("Nuevo Nombre (Enter para mantener): ");
                        String nuevoApellido = MenuHelper.leerTextoOpcional("Nuevo Apellido (Enter para mantener): ");
                        String nuevoMail = MenuHelper.leerTextoOpcional("Nuevo Email (Enter para mantener): ");
                        String nuevoCelular = MenuHelper.leerTextoOpcional("Nuevo Celular (Enter para mantener): ");
                        String nuevaContrasenia = MenuHelper.leerTextoOpcional("Nueva Contrasena (Enter para mantener): ");

                        System.out.println("Desea cambiar el Rol? (Enter para mantener actual):");
                        System.out.println("  1. ADMIN");
                        System.out.println("  2. USUARIO");
                        String rolInput = MenuHelper.leerTextoOpcional("Opcion: ");

                        Rol nuevoRol = null;
                        if (rolInput != null) {
                            nuevoRol = (rolInput.equals("1")) ? Rol.ADMIN : Rol.USUARIO;
                        }

                        usuarioService.editar(idEditar, nuevoNombre, nuevoApellido, nuevoMail, nuevoCelular, nuevaContrasenia, nuevoRol);
                        System.out.println("Usuario actualizado correctamente.");
                        break;

                    case 4: // HU-USR-04: Eliminar
                        System.out.println("\n--- Eliminar Usuario /softdelete/ ---");
                        Long idEliminar = MenuHelper.leerLongOpcional("Ingrese el ID del usuario a eliminar: ");

                        String confirma = MenuHelper.leerTexto("esta seguro de dar de baja a este usuario? (S/N): ");
                        if (confirma.equalsIgnoreCase("S")) {
                            usuarioService.eliminar(idEliminar);
                            System.out.println("Usuario dado de baja con exito. No podra usarse para nuevos pedidos.");
                        } else {
                            System.out.println("Operacion cancelada.");
                        }
                        break;

                    case 0:
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("\nERROR: " + e.getMessage());
            }
        }
    }

    // SUBMENU DE PEDIDOS (epica 4)
    private static void menuPedidos() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n---GESTION DE PEDIDOS---");
            System.out.println("1. Listar Todos los Pedidos");
            System.out.println("2. Crear Nuevo Pedido");
            System.out.println("3. Actualizar Estado / Forma de Pago");
            System.out.println("4. Eliminar Pedido");
            System.out.println("0. Volver al menu Principal");

            opcion = MenuHelper.leerEntero("Seleccione: ");

            try {
                switch (opcion) {
                    case 1: // HU-PED-01: Listar
                        List<Pedido> listaPedidos = pedidoService.listarActivos();
                        if (listaPedidos.isEmpty()) {
                            System.out.println("No hay pedidos registrados en el sistema.");
                        } else {
                            System.out.println("\n--- Historial General de Pedidos---");
                            for (Pedido p : listaPedidos) {
                                System.out.println(p); // Usa el toString()
                                System.out.println("------------------------------------------------");
                            }
                        }
                        break;
                    
                    case 2: // HU-PED-02: Crear Pedido
                        System.out.println("\n--- Nuevo Pedido ---");

                        // Lista usuarios para facilitar la seleccion
                        System.out.println("Clientes activos:");
                        for (Usuario u : usuarioService.listarActivos()) {
                            System.out.println("  ID: " + u.getId() + " - " + u.getNombre() + " " + u.getApellido());
                        }
                        Long usuarioId = MenuHelper.leerLongOpcional("Ingrese el ID del Cliente: ");

                        // Seleccion de forma de pago
                        System.out.println("Seleccione la Forma de Pago:");
                        System.out.println("1. TARJETA");
                        System.out.println("2. TRANSFERENCIA");
                        System.out.println("3. EFECTIVO");
                        int pagoOp = MenuHelper.leerEntero("Opcion: ");
                        FormaPago formaPago = FormaPago.EFECTIVO;
                        if (pagoOp == 1) {
                            formaPago = FormaPago.TARJETA;
                        }
                        if (pagoOp == 2) {
                            formaPago = FormaPago.TRANSFERENCIA;
                        }

                        // Listas temporales para acumular los items que quiere comprar
                        List<Long> productosIds = new ArrayList<>();
                        List<Integer> cantidades = new ArrayList<>();

                        boolean cargandoProductos = true;
                        while (cargandoProductos) {
                            System.out.println("\nCatalogo de Productos disponibles:");
                            for (Producto prod : productoService.listarActivos()) {
                                System.out.println("  ID: " + prod.getId() + " - " + prod.getNombre() + " | Precio: $" + prod.getPrecio() + " | Stock: " + prod.getStock());
                            }

                            Long prodId = MenuHelper.leerLongOpcional("Ingrese el ID del producto a agregar (deje vacio para finalizar carga de items): ");

                            if (prodId == null) {
                                if (productosIds.isEmpty()) {
                                    System.out.println("No puede crear un pedido vacio.");
                                    String salir = MenuHelper.leerTexto("Desea cancelar el pedido? (S/N): ");
                                    if (salir.equalsIgnoreCase("S")) {
                                        break; // CORREGIDO: break en vez de return para mantenerte en el menú de pedidos
                                    }
                                    continue;
                                }
                                cargandoProductos = false; // Finaliza el bucle de carga de productos
                            } else {
                                // CORREGIDO: Validación defensiva en caliente antes de pedir la cantidad
                                try {
                                    Producto prodValido = productoService.buscarPorId(prodId);
                                    int cant = MenuHelper.leerEntero("Ingrese la cantidad: ");
                                    
                                    productosIds.add(prodId);
                                    cantidades.add(cant);
                                    System.out.println("Producto '" + prodValido.getNombre() + "' pre-agregado a la lista.");
                                } catch (Exception e) {
                                    System.out.println("\nERROR: " + e.getMessage() + " Intente con un ID valido.");
                                }
                            }
                        }

                        // Si el pedido no quedó vacío (porque no se canceló en el paso anterior), lo guardamos
                        if (!productosIds.isEmpty()) {
                            // Envia las listas al servicio para la validacion atomica y el descuento de stock
                            Pedido nuevoPedido = pedidoService.crear(usuarioId, formaPago, productosIds, cantidades);
                            System.out.println("\nPedido creado con exito. ID generado: " + nuevoPedido.getId());
                            System.out.println(nuevoPedido);
                        }
                        break;

                    case 3: // HU-PED-03: Actualizar Estado o Pago
                        System.out.println("\n--- Actualizar Pedido ---");
                        Long idEditar = MenuHelper.leerLongOpcional("Ingrese el ID del pedido a modificar: ");

                        System.out.println("Desea cambiar el Estado? (Enter para mantener actual):");
                        System.out.println(" 1. PENDIENTE");
                        System.out.println(" 2. CONFIRMADO");
                        System.out.println(" 3. TERMINADO");
                        System.out.println(" 4. CANCELADO");
                        String estadoInput = MenuHelper.leerTextoOpcional("Opcion: ");

                        Estado nuevoEstado = null;
                        if (estadoInput != null) {
                            if (estadoInput.equals("1")) {
                                nuevoEstado = Estado.PENDIENTE;
                            }
                            if (estadoInput.equals("2")) {
                                nuevoEstado = Estado.CONFIRMADO;
                            }
                            if (estadoInput.equals("3")) {
                                nuevoEstado = Estado.TERMINADO;
                            }
                            if (estadoInput.equals("4")) {
                                nuevoEstado = Estado.CANCELADO;
                            }
                        }

                        System.out.println("Desea cambiar la Forma de Pago? (Enter para mantener actual):");
                        System.out.println("  1. TARJETA");
                        System.out.println("  2. TRANSFERENCIA");
                        System.out.println("  3. EFECTIVO");
                        String pagoInput = MenuHelper.leerTextoOpcional("Opcion: ");

                        FormaPago nuevaForma = null;
                        if (pagoInput != null) {
                            if (pagoInput.equals("1")) {
                                nuevaForma = FormaPago.TARJETA;
                            }
                            if (pagoInput.equals("2")) {
                                nuevaForma = FormaPago.TRANSFERENCIA;
                            }
                            if (pagoInput.equals("3")) {
                                nuevaForma = FormaPago.EFECTIVO;
                            }
                        }

                        pedidoService.actualizarEstadoYPago(idEditar, nuevoEstado, nuevaForma);
                        System.out.println("Pedido actualizado correctamente.");
                        break;

                    case 4: // HU-PED-04: Eliminar, soft delete
                        System.out.println("\n--- Eliminar Pedido ---");
                        Long idEliminar = MenuHelper.leerLongOpcional("Ingrese el ID del pedido a eliminar: ");

                        String confirma = MenuHelper.leerTexto("Esta seguro de ocultar este pedido del historial activo? (S/N): ");
                        if (confirma.equalsIgnoreCase("S")) {
                            pedidoService.eliminar(idEliminar);
                            System.out.println("Pedido eliminado logicamente de los listados activos.");
                        } else {
                            System.out.println("Operacion cancelada.");
                        }
                        break;

                    case 0:
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("\nERROR: " + e.getMessage());
            }
        }
    }

}
