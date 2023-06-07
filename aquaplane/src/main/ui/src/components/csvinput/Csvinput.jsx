import React, { useRef, useState, useCallback } from 'react'
import Dropzone from './dropzone'
import TextField from '@mui/material/TextField';
import './csvinput.css'
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';

const API_ADD_ARGUMENTPAIR = "http://localhost:8080/api/v1/argumentpair/add";
const API_AQUAPLANEIFY = "http://localhost:8080/api/v1/argumentpair/aquaplaneify";

const Csvinput = () => {
  const form = useRef();
  const [files, setFiles] = useState([]);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onDrop = useCallback(files => setFiles(files), [setFiles]);

  const submit = (event) => {
    event.preventDefault();
    setLoading(true);

    const form = event.target;

    // get file and read arg1 and arg2 from csv
    const file = files[0];
    console.log(file);
    var reader = new FileReader();
    var csvString = ""
    reader.onloadend = function(e) {
      csvString = e.target.result
      const csvStringArray = csvString.split(";");
      const arg1 = csvStringArray[0];
      const arg2 = csvStringArray[1];

      const claim = form.elements['claim'].value;

      const argumentPair = {arg1, arg2, claim}
      saveArgumentPair(argumentPair);
      aquaplaneify(argumentPair);
    }
    reader.readAsText(file);
  };

  async function saveArgumentPair(argumentPair) {
    return fetch(API_ADD_ARGUMENTPAIR,{
            method: "POST",
            headers: {"Content-type": "application/json"},
            body: JSON.stringify(argumentPair)
    }).then(() => {
        console.log("New argument pair added.")
    })
  }

  function aquaplaneify(argumentPair) {
    return fetch(API_AQUAPLANEIFY,{
        method: "POST",
        headers: {"Content-type": "application/json"},
        body: JSON.stringify(argumentPair)
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(
                "HTTP error: Status ${response.status}"
            )
        }
        return response.json();
    })
    .then((data) => { 
        console.log(JSON.stringify(data))
        console.log(data)
        setData(data)
        setError(null)
    })
    .catch((err) => { 
        setError(err.message)
        setData(null)
    })
    .finally(() => {
        setLoading(false);
    });
  }

  const reset = (event) => {
    setData(null);
    setError(null);
    setFiles([])
    event.target.reset();
  }

  function downloadJSON() {
    const fileName = "aquaplane.json";
    const blob = new Blob([JSON.stringify(data, null, 4)], { type: "text/json" });
    const jsonURL = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    document.body.appendChild(link);
    link.href = jsonURL;
    link.setAttribute("download", fileName);
    link.click();
    document.body.removeChild(link);
  }


  return (
    <form ref={form} onSubmit={submit} onReset={reset}>
      <div>
        <div className="claim__container">
          <TextField 
              id="outlined-multiline-flexible" 
              helperText="Please enter the claim to which the arguments relate."
              multiline 
              fullWidth 
              maxRows={3} 
              label="Claim" 
              name='claim' 
              variant="outlined" 
              required
              size='small'
          />
        </div>
        <Dropzone onDrop={onDrop}/>
        {
        files.length > 0 
        ? (function() {
            if (loading) {
              return <div className='loading__container'>
                  <CircularProgress/>
              </div>
            }
            if (error) {
              return <div className='alert__container'>
                  <Alert severity="error">There is a problem fetching the data!</Alert>
              </div>
            }
            if (data) {
              return <div className='button_row__container'>
                  <button type="reset" className='btn'>Clear all</button>
                  &nbsp;&nbsp;&nbsp;
                  <button type="button" className='btn btn-primary' onClick={downloadJSON}>Download json</button>
              </div>
            } else {
              return <div className='buttons__container'>
                  <button type='submit' className='btn btn-primary'>AQUAPLANEify</button>
              </div>
            }
          })()
        : <div/>
        }
      </div>
    </form>
  )
}

export default Csvinput