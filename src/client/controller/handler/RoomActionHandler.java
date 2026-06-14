package client.controller.handler;

import client.controller.RoomsController;
import client.view.dialog.AddRoomDialog;
import client.view.panel.RoomsPanel;
import common.model.Room;
import client.service.RoomService;

import javax.swing.*;
import java.awt.Frame;
import java.util.List;

public class RoomActionHandler {

    public static void openAddRoomDialog(RoomsPanel view, List<Room> cachedRooms, Runnable reloadCallback) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        AddRoomDialog dialog = new AddRoomDialog(parent);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String code = dialog.getCode();
            String area = dialog.getArea();
            String type = dialog.getRoomType();
            String priceStr = dialog.getPrice();
            String capacity = dialog.getCapacity();
            String desc = dialog.getDesc();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập Mã phòng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double tempPrice = 0;
            try {
                tempPrice = Double.parseDouble(priceStr.replaceAll("[^\\d]", ""));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final double price = tempPrice;

            String actualRoomId = area + "-" + code;

            if (cachedRooms != null) {
                for (Room r : cachedRooms) {
                    if (r.getRoomId().equalsIgnoreCase(actualRoomId)) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi: Mã phòng '" + code + "' đã tồn tại trong '" + area + "'!", "Trùng lặp dữ liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            int capInt = 2;
            try { capInt = Integer.parseInt(capacity); } catch (Exception ex) {}

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang lưu...");

            final int finalCap = capInt;
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return RoomService.addRoom(actualRoomId, "Trống", price, 20.0, area, type, finalCap, desc);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            reloadCallback.run();
                            JOptionPane.showMessageDialog(dialog, "Đã lưu thông tin phòng mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, result, "Lỗi khi lưu phòng", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }

    public static void handleEditRoom(RoomsPanel view, List<Room> cachedRooms, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String displayCode = (String) view.getTable().getValueAt(selectedRow, 0);
        String area = (String) view.getTable().getValueAt(selectedRow, 1);
        String type = (String) view.getTable().getValueAt(selectedRow, 2);
        String priceStr = (String) view.getTable().getValueAt(selectedRow, 3);
        String capacity = (String) view.getTable().getValueAt(selectedRow, 4);
        String status = (String) view.getTable().getValueAt(selectedRow, 5);
        String desc = (String) view.getTable().getValueAt(selectedRow, 6);

        String realRoomId = displayCode;
        if (cachedRooms != null) {
            for (Room r : cachedRooms) {
                String dCode = r.getRoomId().contains("-") ? r.getRoomId().substring(r.getRoomId().indexOf("-") + 1) : r.getRoomId();
                if (dCode.equals(displayCode) && r.getZone().equals(area)) {
                    realRoomId = r.getRoomId();
                    break;
                }
            }
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        AddRoomDialog dialog = new AddRoomDialog(parent);
        dialog.setTitleText("Sửa Thông Tin Phòng");
        
        dialog.setCode(displayCode);
        dialog.setArea(area);
        dialog.setRoomType(type);
        dialog.setPrice(priceStr.replace(".", "")); 
        dialog.setCapacity(capacity);
        dialog.setDesc(desc);

        final String finalRoomId = realRoomId;
        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String newArea = dialog.getArea();
            String newType = dialog.getRoomType();
            String newPriceStr = dialog.getPrice();
            String newCapacity = dialog.getCapacity();
            String newDesc = dialog.getDesc();

            double tempPrice = 0;
            try {
                tempPrice = Double.parseDouble(newPriceStr.replaceAll("[^\\d]", ""));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final double finalPrice = tempPrice;

            int newCapInt = 2;
            try { newCapInt = Integer.parseInt(newCapacity); } catch (Exception ex) {}
            final int finalNewCap = newCapInt;
            
            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang lưu...");

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return RoomService.updateRoom(finalRoomId, status, finalPrice, 20.0, newArea, newType, finalNewCap, newDesc);
                }
                
                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Lưu Lại");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            reloadCallback.run();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, result, "Lỗi khi cập nhật", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }

    public static void handleDeleteRoom(RoomsPanel view, List<Room> cachedRooms, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String displayCode = (String) view.getTable().getValueAt(selectedRow, 0);
        String area = (String) view.getTable().getValueAt(selectedRow, 1);
        String realRoomId = displayCode;
        if (cachedRooms != null) {
            for (Room r : cachedRooms) {
                String dCode = r.getRoomId().contains("-") ? r.getRoomId().substring(r.getRoomId().indexOf("-") + 1) : r.getRoomId();
                if (dCode.equals(displayCode) && r.getZone().equals(area)) {
                    realRoomId = r.getRoomId();
                    break;
                }
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Bạn có chắc chắn muốn xóa phòng " + displayCode + " (" + area + ")?\nLưu ý: Không thể xóa nếu phòng đang có hợp đồng hoặc khách thuê.", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            final String finalRealRoomId = realRoomId;
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return RoomService.deleteRoom(finalRealRoomId);
                }
                
                @Override
                protected void done() {
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            reloadCallback.run();
                            JOptionPane.showMessageDialog(view, "Đã xóa phòng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(view, result, "Lỗi khi xóa", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    public static void handleMaintenanceRoom(RoomsPanel view, List<Room> cachedRooms, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng để bảo trì/phục hồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String displayCode = (String) view.getTable().getValueAt(selectedRow, 0);
        String area = (String) view.getTable().getValueAt(selectedRow, 1);
        String status = (String) view.getTable().getValueAt(selectedRow, 5);

        String realRoomId = displayCode;
        if (cachedRooms != null) {
            for (Room r : cachedRooms) {
                String dCode = r.getRoomId().contains("-") ? r.getRoomId().substring(r.getRoomId().indexOf("-") + 1) : r.getRoomId();
                if (dCode.equals(displayCode) && r.getZone().equals(area)) {
                    realRoomId = r.getRoomId();
                    break;
                }
            }
        }

        if ("Đang thuê".equalsIgnoreCase(status) || "Đã đầy".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(view, "Không thể bảo trì phòng đang có khách thuê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String finalRoomId = realRoomId;
        String newStatus = "Đang bảo trì".equalsIgnoreCase(status) ? "Trống" : "Đang bảo trì";
        String confirmMsg = "Đang bảo trì".equalsIgnoreCase(status) 
            ? "Bạn muốn phục hồi phòng " + displayCode + " để cho thuê lại?" 
            : "Bạn muốn chuyển phòng " + displayCode + " sang trạng thái BẢO TRÌ?";

        int confirm = JOptionPane.showConfirmDialog(view, confirmMsg, "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    Room targetRoom = null;
                    if (cachedRooms != null) {
                        for (Room r : cachedRooms) {
                            if (r.getRoomId().equals(finalRoomId)) {
                                targetRoom = r;
                                break;
                            }
                        }
                    }
                    if (targetRoom != null) {
                        return RoomService.updateRoom(
                            targetRoom.getRoomId(), 
                            newStatus, 
                            targetRoom.getPrice(), 
                            targetRoom.getArea(), 
                            targetRoom.getZone(), 
                            targetRoom.getType(), 
                            targetRoom.getCapacity(), 
                            targetRoom.getDescription()
                        );
                    }
                    return "Lỗi: Không tìm thấy thông tin phòng trong bộ đệm.";
                }

                @Override
                protected void done() {
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            reloadCallback.run();
                        } else {
                            JOptionPane.showMessageDialog(view, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
