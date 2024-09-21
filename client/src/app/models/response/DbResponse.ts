import { ServerResponse } from "http";
import { Db } from "../db.model";

export interface DbResponse extends ServerResponse {
    result: Db;
}