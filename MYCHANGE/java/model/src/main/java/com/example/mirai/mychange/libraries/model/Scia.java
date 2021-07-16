package com.example.mirai.projectname.libraries.model;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Objects;

public class Scia extends MyChangeEvent {

    public Scia(String jsonData, String rootObjectName, String contextType) {
        super(jsonData, rootObjectName, contextType);
    }

    public Scia(String jsonData,  String contextType) {
        super(jsonData, "scia", contextType);
    }

    @Override
    public String getType() {
        return "SCIA";
    }

    public Float getSuppChainAdjustment() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".supply_chain_adjustment");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getPartOrToolScrapFactoryWarehouseOrWip() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".part_or_tool_scrap_factory_warehouse_or_wip");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getFactoryChangeOrderCostWip() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".factory_change_order_cost_wip");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getPartOrToolScrapFieldWarehouse() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".part_or_tool_scrap_field_warehouse");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getFieldChangeOrderCost() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".field_change_order_cost");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getInventory() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".inventory");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getFieldInvestment() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".field_investment");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getSupplyChainManagementInvestment() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".supply_chain_management_investment");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getInventoryAtRiskValue() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".inventory_at_risk_value");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getInventoryAtRiskReductionProposal() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".inventory_at_risk_reduction_proposal");
        return Objects.isNull(d) ? null : d.floatValue();
    }

    public Float getInventoryAtRiskReductionProposalCost() {
        Double d = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".inventory_at_risk_reduction_proposal_cost");
        return Objects.isNull(d) ? null : d.floatValue();
    }
}
