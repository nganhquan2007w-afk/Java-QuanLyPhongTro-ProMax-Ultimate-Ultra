package client.controller.handler;

import client.controller.TenantsController;
import client.view.dialog.AddTenantDialog;
import client.view.dialog.EditTenantDialog;
import client.view.panel.TenantsPanel;
import client.service.TenantService;
import client.service.RoomService;

import javax.swing.*;
import java.awt.Frame;

public class TenantActionHandler {

    public static void openAddTenantDialog(TenantsController controller, TenantsPanel view, Runnable reloadCallback, java.util.function.BiConsumer<String, String> onTenantAddedCallback) {
        String[] roomIds = controller.loadRoomIdsWithStatus(null);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        AddTenantDialog dialog = new AddTenantDialog(parent, roomIds);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String name      = dialog.getTenantName();
            String phone     = dialog.getPhone();
            String cccd      = dialog.getCCCD();
            String gender    = dialog.getGender();
            String birthDate = dialog.getBirthDate();
            String address   = dialog.getAddress();
            String roomDisplay = dialog.getRoom();
            String room      = controller.getRealRoomId(roomDisplay);
            String startDate = dialog.getStartDate();
            String duration  = dialog.getDuration();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền Họ tên và Số điện thoại!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (room.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn Phòng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang lưu...");

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return TenantService.addTenant(name, phone, cccd, gender, birthDate, address, room, startDate, duration);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if (result != null && result.startsWith("SUCCESS")) {
                            reloadCallback.run();

                            // Lấy mật khẩu nếu server trả về
                            String plainPw = result.contains(":") ? result.substring(result.indexOf(":") + 1) : null;

                            // Hiển thị dialog thông tin tài khoản với nút sao chép
                            showAccountCreatedDialog(dialog, phone, plainPw);

                            dialog.dispose();
                            if (onTenantAddedCallback != null) {
                                onTenantAddedCallback.accept(room, name);
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "❌ " + result, "Lỗi khi đăng ký", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }

    public static void handleDeleteTenant(TenantsController controller, TenantsPanel view, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách thuê cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenantId = (String) view.getTable().getModel().getValueAt(selectedRow, 0);
        String name = (String) view.getTable().getModel().getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa khách thuê " + name + " không?\nCảnh báo: Hành động này sẽ vô hiệu hóa tất cả hợp đồng của khách thuê này.", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String result;
            try {
                int id = Integer.parseInt(tenantId.replace("KH", ""));
                result = TenantService.deleteTenant(id);
            } catch (NumberFormatException ex) {
                result = "ID khách thuê không hợp lệ";
            }
            if ("SUCCESS".equals(result)) {
                reloadCallback.run();
                JOptionPane.showMessageDialog(view, "Đã xóa khách thuê thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, result, "Lỗi khi xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void handleEditTenant(TenantsController controller, TenantsPanel view, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách thuê cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenantIdStr = (String) view.getTable().getModel().getValueAt(selectedRow, 0);
        int tenantId = Integer.parseInt(tenantIdStr.replace("KH", ""));

        java.util.List<String[]> tenants = TenantService.getTenants();
        String[] targetTenant = null;
        for (String[] t : tenants) {
            if (t[0].equals(String.valueOf(tenantId))) {
                targetTenant = t;
                break;
            }
        }

        if (targetTenant == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu khách thuê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fullName = targetTenant[1];
        String phone = targetTenant[2];
        String cccd = targetTenant[3];
        String gender = targetTenant[4];
        String birthDate = targetTenant[5];
        String address = targetTenant[6];
        String currentRoom = targetTenant[7];

        String currentRoomDisplay = currentRoom;
        if (currentRoom != null && !currentRoom.isEmpty()) {
            java.util.List<common.model.Room> rooms = RoomService.getRooms();
            if (rooms != null) {
                for (common.model.Room r : rooms) {
                    if (r.getRoomId().equals(currentRoom)) {
                        String area = "Khu nhà A";
                        if (r.getDescription() != null && r.getDescription().contains("::")) {
                            area = r.getDescription().split("::")[0];
                        }
                        String code = currentRoom.contains("-") ? currentRoom.substring(currentRoom.indexOf("-") + 1) : currentRoom;
                        currentRoomDisplay = "P." + code + " - " + area;
                        break;
                    }
                }
            }
        }

        String[] roomIds = controller.loadRoomIdsWithStatus(currentRoom);

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        EditTenantDialog dialog = new EditTenantDialog(parent, tenantId, fullName, phone, cccd, gender, birthDate, address, currentRoomDisplay, roomIds);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String newName = dialog.getTenantName();
            String newPhone = dialog.getPhone();
            String newCCCD = dialog.getCCCD();
            String newGender = dialog.getGender();
            String newBirthDate = dialog.getBirthDate();
            String newAddress = dialog.getAddress();
            String newRoomDisplay = dialog.getRoom();
            String newRoom = controller.getRealRoomId(newRoomDisplay);

            if (newName.isEmpty() || newPhone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền Họ tên và Số điện thoại!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang lưu...");

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return TenantService.updateTenant(tenantId, newName, newPhone, newCCCD, newGender, newBirthDate, newAddress, newRoom);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            reloadCallback.run();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật thông tin khách thuê thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "❌ " + result, "Lỗi khi cập nhật", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }

    /**
     * Hiển thị dialog thông tin tài khoản vừa tạo, kèm nút sao chép một click.
     * @param parent  cửa sổ cha
     * @param username  tên đăng nhập (SĐT)
     * @param plainPw   mật khẩu plain-text (null nếu user đã tồn tại)
     */
    private static void showAccountCreatedDialog(java.awt.Component parent, String username, String plainPw) {
        // Panel chính
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new java.awt.GridLayout(0, 1, 0, 8));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));

        javax.swing.JLabel lblTitle = new javax.swing.JLabel("Đăng ký khách thuê thành công!");
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblTitle.setForeground(new java.awt.Color(22, 163, 74));
        panel.add(lblTitle);

        panel.add(new javax.swing.JLabel(" "));

        javax.swing.JLabel lblDesc = new javax.swing.JLabel("Thông tin tài khoản đăng nhập của khách thuê:");
        lblDesc.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        panel.add(lblDesc);

        // Row tên đăng nhập
        javax.swing.JPanel rowUser = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        rowUser.setOpaque(false);
        javax.swing.JLabel lblUserKey = new javax.swing.JLabel("  Tên đăng nhập:  ");
        lblUserKey.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        javax.swing.JTextField txtUser = new javax.swing.JTextField(username, 18);
        txtUser.setEditable(false);
        txtUser.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 13));
        txtUser.setBackground(new java.awt.Color(241, 245, 249));
        txtUser.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(203, 213, 225)),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        rowUser.add(lblUserKey);
        rowUser.add(txtUser);
        panel.add(rowUser);

        // Row mật khẩu
        String pwDisplay = (plainPw != null && !plainPw.isEmpty()) ? plainPw : "(đã tồn tại — không đổi)";
        javax.swing.JPanel rowPw = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        rowPw.setOpaque(false);
        javax.swing.JLabel lblPwKey = new javax.swing.JLabel("  Mật khẩu:          ");
        lblPwKey.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        javax.swing.JTextField txtPw = new javax.swing.JTextField(pwDisplay, 18);
        txtPw.setEditable(false);
        txtPw.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 13));
        txtPw.setBackground(new java.awt.Color(241, 245, 249));
        txtPw.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(203, 213, 225)),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        rowPw.add(lblPwKey);
        rowPw.add(txtPw);
        panel.add(rowPw);

        panel.add(new javax.swing.JLabel(" "));

        // Nút sao chép — dùng icon copy.png
        javax.swing.ImageIcon copyIcon = null;
        try {
            java.io.InputStream imgStream = TenantActionHandler.class.getResourceAsStream("/Image/copy.png");
            if (imgStream != null) {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(imgStream);
                copyIcon = new javax.swing.ImageIcon(
                    img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}

        javax.swing.JButton btnCopy = (copyIcon != null)
            ? new javax.swing.JButton("  Sao chép thông tin tài khoản", copyIcon)
            : new javax.swing.JButton("Sao chép thông tin tài khoản");
        btnCopy.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnCopy.setBackground(new java.awt.Color(59, 130, 246));
        btnCopy.setForeground(java.awt.Color.WHITE);
        btnCopy.setFocusPainted(false);
        btnCopy.setBorderPainted(false);
        btnCopy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCopy.setOpaque(true);
        btnCopy.setIconTextGap(6);

        String copyText = "Tên đăng nhập: " + username + " | Mật khẩu: " + pwDisplay;
        btnCopy.addActionListener(e -> {
            try {
                java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(copyText);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                btnCopy.setText("✅  Đã sao chép!");
                btnCopy.setBackground(new java.awt.Color(22, 163, 74));
            } catch (Exception ex) {
                btnCopy.setText("❌  Không thể sao chép");
            }
        });
        panel.add(btnCopy);

        javax.swing.JOptionPane.showMessageDialog(
            parent, panel, "Tạo Tài Khoản Thành Công", javax.swing.JOptionPane.PLAIN_MESSAGE);
    }
}
