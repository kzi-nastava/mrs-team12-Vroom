import { MapActionType } from "../enums/map-action-type.enum";

export interface MapAction {
  type: MapActionType;
  payload?: any;
}
