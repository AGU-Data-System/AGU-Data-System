import * as React from "react";
import {List, ListItem, ListItemText, Paper} from "@mui/material";

export function ControlMenuLeft() {
    return (
        <Paper sx={{ maxHeight: 800, overflow: 'auto' }}>
            <List>
                {/* Your list items */}
                <ListItem>
                    <ListItemText primary="Item 1" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 2" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 3" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 4" />
                </ListItem>
                {/* Add more list items as needed */}
            </List>
        </Paper>
    );
}

export function ControlMenuRight() {
    return (
        <Paper sx={{ maxHeight: 800, overflow: 'auto' }}>
            <List>
                {/* Your list items */}
                <ListItem>
                    <ListItemText primary="Item 1" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 2" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 3" />
                </ListItem>
                <ListItem>
                    <ListItemText primary="Item 4" />
                </ListItem>
                {/* Add more list items as needed */}
            </List>
        </Paper>
    );
}