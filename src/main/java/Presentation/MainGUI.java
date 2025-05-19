package Presentation;

import javax.swing.*;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductGUI productGUI = new ProductGUI();
            OrderGUI orderGUI = new OrderGUI(productGUI);
            ClientGUI clientGUI = new ClientGUI(orderGUI);

            productGUI.setOrderGUI(orderGUI);

            clientGUI.setLocation(25, 250);
            productGUI.setLocation(525, 250);
            orderGUI.setLocation(1025, 250);
        });
    }

}
