import React, { useState, useEffect } from "react";

const API_URL = "http://localhost:8080";

interface LaneState {
  laneDirection: string[] | string;
  lightColor: string;
  vehicles: string[];
}

interface RoadState {
  incomingDirection: string;
  lanes: LaneState[];
}

interface IntersectionState {
  roads: RoadState[];
}

interface RoadProps {
  arm: string;
  data?: RoadState;
  exitLanesCount: number;
}

export default function TrafficSimulation(): JSX.Element {
  const [state, setState] = useState<IntersectionState>({ roads: [] });
  const [loading, setLoading] = useState<boolean>(true);

  const fetchState = async (): Promise<void> => {
    try {
      const response = await fetch(`${API_URL}/state`);
      if (!response.ok) {
        throw new Error(`HTTP error from backend: ${response.status}`);
      }
      const data = await response.json();
      setState(data);
    } catch (error) {
      console.error("Error fetching state from Java:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleNext = async (): Promise<void> => {
    try {
      const response = await fetch(`${API_URL}/next`, { method: "POST" });
      if (!response.ok) {
        throw new Error(`HTTP error from backend: ${response.status}`);
      }
      const data = await response.json();
      setState(data);
    } catch (error) {
      console.error("Error executing next step:", error);
    }
  };

  useEffect(() => {
    fetchState();
  }, []);

  const getRoad = (direction: string): RoadState | undefined => {
    if (!state || !Array.isArray(state.roads)) return undefined;
    return state.roads.find((r) => r && r.incomingDirection === direction);
  };

  const getOppositeDirection = (dir: string): string => {
    switch (dir) {
      case "NORTH":
        return "SOUTH";
      case "SOUTH":
        return "NORTH";
      case "EAST":
        return "WEST";
      case "WEST":
        return "EAST";
      default:
        return "";
    }
  };

  const getExitLanesCount = (armDirection: string): number => {
    if (!state || !Array.isArray(state.roads)) return 1;

    const oppositeDir = getOppositeDirection(armDirection);
    const oppositeRoad = state.roads.find(
      (r) => r && r.incomingDirection === oppositeDir,
    );

    if (!oppositeRoad || !Array.isArray(oppositeRoad.lanes)) return 1;

    const count = oppositeRoad.lanes.filter((lane) => {
      if (!lane.laneDirection) return false;

      // Jeśli Java przesyła tablicę
      if (Array.isArray(lane.laneDirection)) {
        return lane.laneDirection.some(
          (d) =>
            typeof d === "string" && d.toUpperCase().includes(armDirection),
        );
      }

      // Jeśli Java przesyła Stringa
      if (typeof lane.laneDirection === "string") {
        return lane.laneDirection.toUpperCase().includes(armDirection);
      }

      return false;
    }).length;

    return count > 0 ? count : 1;
  };

  if (loading)
    return (
      <div style={{ color: "white", padding: "20px" }}>Loading data...</div>
    );

  const hasValidData =
    state && Array.isArray(state.roads) && state.roads.length > 0;

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <button onClick={handleNext} style={styles.button}>
          NEXT STEP
        </button>
      </div>

      {!hasValidData ? (
        <div style={styles.errorMessage}>
          <h3>No intersection data!</h3>
          <p>Make sure that:</p>
          <ol>
            <li>The Java server is running.</li>
            <li>
              You added the line allowing CORS in the Main class: <br />
              <code>
                app.before(ctx -{">"} ctx.header("Access-Control-Allow-Origin",
                "*"));
              </code>
            </li>
            <li>
              Press <strong>F12</strong> in your browser and check the{" "}
              <strong>Console</strong> tab for more details.
            </li>
          </ol>
        </div>
      ) : (
        <div style={styles.intersectionGrid}>
          <div className="empty-nw" />
          <Road
            arm="NORTH"
            data={getRoad("NORTH")}
            exitLanesCount={getExitLanesCount("NORTH")}
          />
          <div className="empty-ne" />

          <Road
            arm="WEST"
            data={getRoad("WEST")}
            exitLanesCount={getExitLanesCount("WEST")}
          />
          <div style={styles.centerBox}>INTERSECTION</div>
          <Road
            arm="EAST"
            data={getRoad("EAST")}
            exitLanesCount={getExitLanesCount("EAST")}
          />

          <div className="empty-sw" />
          <Road
            arm="SOUTH"
            data={getRoad("SOUTH")}
            exitLanesCount={getExitLanesCount("SOUTH")}
          />
          <div className="empty-se" />
        </div>
      )}
    </div>
  );
}

function Road({ arm, data, exitLanesCount }: RoadProps): JSX.Element {
  const lanes = Array.isArray(data?.lanes) ? data.lanes : [];

  const displayLanes =
    arm === "NORTH" || arm === "EAST" ? [...lanes].reverse() : lanes;

  const inboundLanesCount = displayLanes.length > 0 ? displayLanes.length : 1;
  const exitLanesArray = Array.from({ length: exitLanesCount });

  let roadFlexDirection: "row" | "column" = "row";
  let laneContainerFlexDirection: "row" | "column" = "row";
  let isInboundFirst = true;
  let laneFlexDirection: "row" | "column" | "row-reverse" | "column-reverse" =
    "column";
  let vehiclesFlexDirection:
    | "row"
    | "column"
    | "row-reverse"
    | "column-reverse" = "column";

  switch (arm) {
    case "NORTH":
      roadFlexDirection = "row";
      laneContainerFlexDirection = "row";
      isInboundFirst = true;
      laneFlexDirection = "column-reverse";
      vehiclesFlexDirection = "column-reverse";
      break;
    case "SOUTH":
      roadFlexDirection = "row";
      laneContainerFlexDirection = "row";
      isInboundFirst = false;
      laneFlexDirection = "column";
      vehiclesFlexDirection = "column";
      break;
    case "WEST":
      roadFlexDirection = "column";
      laneContainerFlexDirection = "column";
      isInboundFirst = false;
      laneFlexDirection = "row-reverse";
      vehiclesFlexDirection = "row-reverse";
      break;
    case "EAST":
      roadFlexDirection = "column";
      laneContainerFlexDirection = "column";
      isInboundFirst = true;
      laneFlexDirection = "row";
      vehiclesFlexDirection = "row";
      break;
  }

  const exitLanes = (
    <div
      style={{
        ...styles.laneContainer,
        flexDirection: laneContainerFlexDirection,
        opacity: 0.5,
        flex: exitLanesCount,
      }}
    >
      {exitLanesArray.map((_, index) => (
        <div key={`exit-${index}`} style={styles.exitLane}></div>
      ))}
    </div>
  );

  const inboundLanes = (
    <div
      style={{
        ...styles.laneContainer,
        flexDirection: laneContainerFlexDirection,
        flex: inboundLanesCount,
      }}
    >
      {displayLanes.map((lane, index) => {
        const vehicles = Array.isArray(lane?.vehicles) ? lane.vehicles : [];
        const lightStatus =
          typeof lane?.lightColor === "string"
            ? lane.lightColor.toUpperCase()
            : "";
        const isRightArrow = lightStatus === "GREEN_ARROW_RIGHT";

        return (
          <div
            key={index}
            style={{ ...styles.lane, flexDirection: laneFlexDirection }}
          >
            <div style={styles.trafficLightContainer}>
              {isRightArrow ? (
                <div
                  style={{
                    ...styles.light,
                    border: "none",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <span
                    style={{
                      color: "green",
                      fontSize: "20px",
                      fontWeight: "bold",
                    }}
                  >
                    {getArrowCharacterForDirection(arm, "RIGHT")}
                  </span>
                </div>
              ) : (
                <div
                  style={{
                    ...styles.light,
                    backgroundColor: getLightColor(lane?.lightColor),
                  }}
                />
              )}
              <span
                style={{
                  fontSize: "16px",
                  fontWeight: "bold",
                  textAlign: "center",
                  lineHeight: "1.2",
                }}
              >
                {getDirectionArrows(lane?.laneDirection)}
              </span>
            </div>

            <div
              style={{
                ...styles.vehiclesArea,
                flexDirection: vehiclesFlexDirection,
              }}
            >
              {vehicles.map((vId) => (
                <div
                  key={String(vId)}
                  style={styles.vehicle}
                  title={String(vId)}
                />
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );

  return (
    <div style={{ ...styles.roadConfig, flexDirection: roadFlexDirection }}>
      {isInboundFirst ? (
        <>
          {inboundLanes}
          {exitLanes}
        </>
      ) : (
        <>
          {exitLanes}
          {inboundLanes}
        </>
      )}
    </div>
  );
}

function getArrowCharacterForDirection(
  arm: string,
  turnDirection: string,
): string {
  if (turnDirection !== "RIGHT") return "";

  switch (arm) {
    case "NORTH":
      return "←";
    case "SOUTH":
      return "→";
    case "WEST":
      return "↓";
    case "EAST":
      return "↑";
    default:
      return "→";
  }
}

function getLightColor(status: string | undefined): string {
  if (typeof status !== "string") return "gray";
  const s = status.toUpperCase();
  if (s === "GREEN_ARROW_RIGHT") return "transparent";
  if (s.includes("RED")) return "red";
  if (s.includes("YELLOW")) return "orange";
  if (s.includes("GREEN")) return "green";
  return "gray";
}

function getDirectionArrows(directions: string[] | string | undefined): string {
  if (!directions) return "";

  const dirArray = Array.isArray(directions)
    ? directions
    : typeof directions === "string"
      ? directions.split(/[, \[\\]]+/).filter(Boolean)
      : [];

  return dirArray
    .map((direction) => {
      if (typeof direction !== "string") return "";
      const d = direction.toUpperCase();
      if (d.includes("NORTH")) return "↑";
      if (d.includes("SOUTH")) return "↓";
      if (d.includes("EAST")) return "→";
      if (d.includes("WEST")) return "←";
      return direction;
    })
    .join("");
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    fontFamily: "sans-serif",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    backgroundColor: "#1e1e1e",
    color: "white",
    minHeight: "100vh",
    padding: "20px",
  },
  header: {
    marginBottom: "40px",
  },
  button: {
    padding: "10px 20px",
    fontSize: "16px",
    cursor: "pointer",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "4px",
    fontWeight: "bold",
  },
  errorMessage: {
    backgroundColor: "#ff4444",
    padding: "20px",
    borderRadius: "8px",
    maxWidth: "500px",
  },
  intersectionGrid: {
    display: "grid",
    gridTemplateColumns: "250px 250px 250px",
    gridTemplateRows: "250px 250px 250px",
    gap: "10px",
  },
  centerBox: {
    backgroundColor: "#333",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    border: "2px dashed #666",
  },
  roadConfig: {
    display: "flex",
    backgroundColor: "#444",
    border: "1px solid #555",
  },
  laneContainer: {
    display: "flex",
    flex: 1,
  },
  exitLane: {
    flex: 1,
    border: "1px dashed #777",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "10px",
    color: "#aaa",
  },
  lane: {
    flex: 1,
    border: "1px solid #666",
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-start",
    padding: "5px",
    gap: "10px",
    overflow: "hidden",
  },
  trafficLightContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
  },
  light: {
    width: "15px",
    height: "15px",
    borderRadius: "50%",
    border: "1px solid #000",
  },
  vehiclesArea: {
    display: "flex",
    gap: "5px",
  },
  vehicle: {
    width: "12px",
    height: "12px",
    backgroundColor: "#00d2ff",
    borderRadius: "50%",
    border: "1px solid #fff",
    flexShrink: 0,
  },
};
