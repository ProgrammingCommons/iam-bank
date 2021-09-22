import java.sql.CallableStatement;
import java.sql.Connection;
import sailpoint.api.SailPointContext;
import sailpoint.object.Application;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningResult;
import sailpoint.object.Attributes;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.Schema;

ProvisioningResult result = new ProvisioningResult();

CallableStatement stmt = null;

try {
    stmt = connection.prepareCall("exec database.dbo.IIQ_ENABLEs_USUA @COD_USUA = ?, @IDT_SITE_HOLD = NULL, @IDT_FUNC = NULL, @IDT_EMPRE = NULL, @IND_ATIV = 'S'");
    stmt.setString(1, request.getNativeIdentity());
    stmt.executeUpdate();
    stmt.close();

    result.setStatus(ProvisioningResult.STATUS_COMMITTED);
} catch (Exception e) {
    result.setStatus(ProvisioningResult.STATUS_FAILED);
    result.addError(e);
    log.error(e.getMessage(), e);
} finally {
    stmt = null;
}

if( request instanceof AccountRequest ) {
    AccountRequest accRequest = (AccountRequest) request;
    if(accRequest.getAttributeRequests() != null){
        for (AttributeRequest attributeRequest : accRequest.getAttributeRequests()) {

            String campo = attributeRequest.getName();
            String valor = ""+attributeRequest.getValue();

            CallableStatement stmt = null;

            try{
                stmt = connection.prepateCall("{CALL fw_iiq_modifica_conta(?,?,?)}");
                stmt.setString(1, request.getNativeIdentity());
                stmt.setString(2, campo.toLowerCase());
                stmt.setString(3, valor);

                stmt.executeUpdate();
                stmt.close();

                result.setStatus(ProvisioningResult.STATUS_COMMITTED);
            } catch (Exception e) {
                result.setStatus(ProvisioningResult.STATUS_FAILED);
                result.addError(e);

                log.error(e.getMessage(), e);
            }
        }

    }
}
return result;
