/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.payment.payload;

import bisq.core.locale.Res;

import com.google.protobuf.Message;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
@Getter
@Slf4j
public final class TransferwiseAccountPayload extends PaymentAccountPayload {
    private String email = "";

    public TransferwiseAccountPayload(String paymentMethod, String id) {
        super(paymentMethod, id);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private TransferwiseAccountPayload(String paymentMethod,
                                       String id,
                                       String email,
                                       long maxTradePeriod,
                                       Map<String, String> excludeFromJsonDataMap) {
        super(paymentMethod,
                id,
                maxTradePeriod,
                excludeFromJsonDataMap);

        this.email = email;
    }

    @Override
    public Message toProtoMessage() {
        return getPaymentAccountPayloadBuilder()
                .setTransferwiseAccountPayload(protobuf.TransferwiseAccountPayload.newBuilder().setEmail(email))
                .build();
    }

    public static TransferwiseAccountPayload fromProto(protobuf.PaymentAccountPayload proto) {
        return new TransferwiseAccountPayload(proto.getPaymentMethodId(),
                proto.getId(),
                proto.getTransferwiseAccountPayload().getEmail(),
                proto.getMaxTradePeriod(),
                new HashMap<>(proto.getExcludeFromJsonDataMap()));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getPaymentDetails() {
        return Res.get(paymentMethodId) + " - " + Res.getWithCol("payment.email") + " " + email;
    }

    @Override
    public String getPaymentDetailsForTradePopup() {
        return getPaymentDetails();
    }

    @Override
    public byte[] getAgeWitnessInputData() {
        // holderName will be included as part of the witness data.
        // older accounts that don't have holderName still retain their existing witness.
        return super.getAgeWitnessInputData(ArrayUtils.addAll(
                email.getBytes(StandardCharsets.UTF_8),
                getHolderName().getBytes(StandardCharsets.UTF_8)));
    }
}
