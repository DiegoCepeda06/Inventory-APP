/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Producto;
import modelo.RepositorioProducto;
import vista.VistaInventario;
import java.math.RoundingMode;

/**
 *
 * @author HP
 */
public class ControladorProducto implements ActionListener {

    private RepositorioProducto repositorio;
    private VistaInventario vista;

    public ControladorProducto(RepositorioProducto repositorio, VistaInventario vista) {
        this.repositorio = repositorio;
        this.vista = vista;
        configBotones();
        cargarProductos();
    }

//Creacion del CRUD
    private List<Producto> listar() {
        List<Producto> resultado = (List<Producto>) repositorio.findAll();
        return resultado;
    }

    private Producto listarbyid(Long id) {
        try {
            Producto resultado = repositorio.findById(id).get();
            return resultado;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean crearProducto(String nombre, Double precio, Integer inventario) {
        try {
            Producto nuevoproducto = new Producto(null, nombre, precio, inventario);
            repositorio.save(nuevoproducto);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean actualizarProducto(Long codigo, String nombre, Double precio, Integer inventario) {
        try {
            Producto x = listarbyid(codigo);
            x.setNombre(nombre);
            x.setPrecio(precio);
            x.setInventario(inventario);
            repositorio.save(x);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean eliminar(Long codigo) {
        try {
            repositorio.deleteById(codigo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void cargarProductos() {

        DefaultTableModel model = (DefaultTableModel) vista.getTblinventario().getModel();
        model.setRowCount(0);
        for (Producto x : listar()) {
            model.addRow(new Object[]{x.getCodigo(), x.getNombre(), x.getPrecio(), x.getInventario()});
        }
    }

    private void limpiarCampos() {
        vista.getTxtnombre().setText("");
        vista.getTxtprecio().setText("");
        vista.getTxtinventario().setText("");
    }

    private void configBotones() {
        vista.getBtnmostraractualizar().addActionListener(this);
        vista.getBtnagregar().addActionListener(this);
        vista.getBtnborrar().addActionListener(this);
        vista.getBtninforme().addActionListener(this);
        vista.getVistaActualizar().getBtnactualizar().addActionListener(this);
    }

    private void agregarProducto() {

        try {
            String nombre = vista.getTxtnombre().getText();
            Double precio = Double.parseDouble(vista.getTxtprecio().getText());
            Integer inventario = Integer.parseInt(vista.getTxtinventario().getText());
            crearProducto(nombre, precio, inventario);
            JOptionPane.showMessageDialog(vista, "Agregado con Exito", "--", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Ocurrio un Error", "--", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrar() {
        Long codigoAborrar = (Long) vista.getTblinventario().getValueAt(vista.getTblinventario().getSelectedRow(), 0);
        boolean isBorrar = eliminar(codigoAborrar);
        if (isBorrar) {
            JOptionPane.showMessageDialog(vista, "Eliminado con exito", "--", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo elminar", "--", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarActualizar() {
        Long codigo = (Long) vista.getTblinventario().getValueAt(vista.getTblinventario().getSelectedRow(), 0);
        Producto producto = listarbyid(codigo);
        vista.getVistaActualizar().setProducto(producto);
        vista.getVistaActualizar().setVisible(true);
    }

    private void actualizar() {
        Long codigo = vista.getVistaActualizar().getProducto().getCodigo();
        String nuevoNombre = (vista.getVistaActualizar().getTxtnombre().getText());
        Double nuevoPrecio = (Double.parseDouble(vista.getVistaActualizar().getTxtprecio().getText()));
        Integer nuevoInventario = (Integer.parseInt(vista.getVistaActualizar().getTxtinventario().getText()));

        boolean isUpdate = actualizarProducto(codigo, nuevoNombre, nuevoPrecio, nuevoInventario);
        if (isUpdate) {
            JOptionPane.showMessageDialog(vista, "Inventario Actualizado", "--", JOptionPane.INFORMATION_MESSAGE);
            vista.getVistaActualizar().dispose();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar", "--", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void generarInforme() {
        List<Producto> producto = listar();
        List<String> resultados = getDatosInforme(producto);
        String Informe = "Producto con el precio mayor: " + resultados.get(0) + "\n"
                + "Producto con el precio menor: " + resultados.get(1) + "\n"
                + "Valor promedio de los productos del inventario: " + String.format("%.1f", Double.parseDouble(resultados.get(2))) + "\n"
                + "Valor total del inventario: " + String.format("%.1f", Double.parseDouble(resultados.get(3)));
        JOptionPane.showMessageDialog(vista, Informe, "Informe", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<String> getDatosInforme(List<Producto> productos) {
        List<String> resultados = new ArrayList<>();
        Double promedio = 0d;
        Double valorInventario = 0d;
        Producto mayor = null;
        Producto menor = null;
        for (Producto x : productos) {
            promedio = promedio + x.getPrecio();
            valorInventario = valorInventario + (x.getPrecio() * x.getInventario());
            if (mayor == null || mayor.getPrecio() < x.getPrecio()) {
                mayor = x;
            }
            if (menor == null || menor.getPrecio() > x.getPrecio()) {
                menor = x;
            }
        }
        promedio = promedio / productos.size();
        resultados.add(mayor.getNombre());
        resultados.add(menor.getNombre());
        resultados.add(promedio + "");
        resultados.add(valorInventario + "");

        return resultados;
    }

    @Override
    public void actionPerformed(ActionEvent e
    ) {
//este es el metodo abstracto donde iran lo que hace cada boton
        if (e.getSource() == vista.getBtnagregar()) {
            agregarProducto();
//despues de agregar el valor hace el update de los nuevos valores

        } else if (e.getSource() == vista.getBtnborrar()) {
            borrar();

        } else if (e.getSource() == vista.getBtnmostraractualizar()) {
            enviarActualizar();

        } else if (e.getSource() == vista.getVistaActualizar().getBtnactualizar()) {
            actualizar();
        } else if (e.getSource() == vista.getBtninforme()) {
            generarInforme();
        }
        cargarProductos();
    }
}
