package client.utils.communication.integration;

/**
 * Method for checking if callback was called.
 */
public class CallbackTest {
    private Integer calledCount = 0;
    private Object result = null;

    /**
     * Test method to subscribe to.
     *
     * @param obj payload
     */
    public void callback(Object obj) {
        calledCount++;
        result = obj;
    }

    public Integer getCalledCount() {
        return calledCount;
    }

    public Object getResult() {
        return result;
    }
}
