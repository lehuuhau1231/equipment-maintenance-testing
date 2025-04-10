
import com.qhuong.services.ThietBiServices;
import com.qhuong.services.AdminServices;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author lehuu
 */
public class TestThietBiServices {
    AdminServices admin = new AdminServices();
    ThietBiServices tb = new ThietBiServices();

//    @Test
//    public void testAddThietBi_InvalidAdminId() {
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            tb.addThietBi("Test Device", LocalDate.now(), 1);
//        });
//        assertEquals("Không thể thêm thiết bị vì thiếu thông tin admin", exception.getMessage());
//    }

//    @Test
//    public void testUpdateThietBi_InvalidAdminId() {
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            tb.updateThietBi(1, "Test Device", LocalDate.now(), null, 1);
//        });
//        assertEquals("Không thể cập nhật thiết bị vì thiếu thông tin admin", exception.getMessage());
//    }
//    
    @Test
    void testEmptyTenThietBi_ThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            tb.addThietBi("  ", LocalDate.now(), 2);
        });
        assertEquals("Vui lòng điền đầy đủ thông tin", ex.getMessage());
    }
}
