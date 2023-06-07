import React from "react";
import { useDropzone } from "react-dropzone";
import {BsFileEarmarkArrowUp} from 'react-icons/bs';
import './dropzone.css';
import Button from '@mui/material/Button';

function Dropzone({ onDrop }) {
  const { getRootProps, getInputProps, open, isDragActive, acceptedFiles } = useDropzone({
    accept: {
      'text/csv': ['.csv']
    },
    maxFiles: 1,
    noClick: true,
    onDrop
  });

  const files = acceptedFiles.map((file) => (
    <li key={file.path}>
      {file.path} - {file.size} bytes
    </li>
  ));

  return (
    <div>
      <div className="dropzone__container">
        <div {...getRootProps({ className: "dropzone" })}>
          <input {...getInputProps()} />
          <div className="dropzone-content">
            <BsFileEarmarkArrowUp className="dropzone-icon"/>
            {isDragActive ? <p>Release to drop the file here</p> : <p>Drag and drop file here</p>}
            <small>Files Supported: CSV</small>
          </div>
          <Button variant="outlined" onClick={open} size="medium">
              Choose File
          </Button>
        </div>
        <div className="fileList">
          <ul>{files}</ul>
        </div>
      </div>
    </div>
  );
}
export default Dropzone;