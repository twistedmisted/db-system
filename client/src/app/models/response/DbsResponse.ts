import { ServerResponse } from "http";
import { Page } from "../page.model";
import { Db } from "../db.model";

export interface DbsResponse extends ServerResponse {
    result: Page<Db>;
}