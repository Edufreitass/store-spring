package codeplays.trainee.store.domain.order;

import codeplays.trainee.store.domain.payment.PaymentService;
import codeplays.trainee.store.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserService userService;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        orderService = new OrderService(userService, paymentService, true);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o usuário for menor de idade")
    public void shouldThrowsAnExceptionWhenUserIsMinor() {
        // Arrange
        Order order = new Order(1L);

        when(userService.isUserMinor(1L)).thenReturn(true);
        doNothing().when(paymentService).pay();

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> orderService.create(order));

        // Assert
        assertEquals("O usuário não pode ser menor de idade", exception.getMessage());
        verify(userService, times(1)).isUserMinor(eq(1L));
        verify(paymentService, times(0)).pay();
    }

    @Test
    @DisplayName("Deve liberar para o pagamento quando o usuário for maior de idade")
    public void shouldPayWhenUserMajor() {
        // Arrange
        orderService = new OrderService(userService, paymentService, false);
        Order order = new Order(2L);

        when(userService.isUserMinor(eq(2L))).thenReturn(false);
        doNothing().when(paymentService).pay();

        // Act
        orderService.create(order);

        // Assert
        verify(userService, times(1)).isUserMinor(2L);
        verify(paymentService, times(1)).pay();
    }

}
